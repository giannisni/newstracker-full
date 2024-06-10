import argparse
import requests
import json
from newspaper import Article
from elasticsearch import Elasticsearch
from datetime import datetime, timedelta
from bs4 import BeautifulSoup
import pytz

# Set up command-line argument parsing
parser = argparse.ArgumentParser(description='Process some articles.')
parser.add_argument('--start_date', type=str, help='Start date in YYYY-MM-DD format')
parser.add_argument('--end_date', type=str, help='End date in YYYY-MM-DD format')
parser.add_argument('--domain', type=str, default='cnn.com', help='Domain to query for articles')
parser.add_argument('--index', type=str, default='', help='Index name')
parser.add_argument('--es_host', type=str, default='localhost', help='Elasticsearch host')
parser.add_argument('--es_port', type=int, default=9200, help='Elasticsearch port')
parser.add_argument('--es_scheme', type=str, default='http', help='Elasticsearch scheme')
parser.add_argument('--es_username', type=str, help='Elasticsearch username')
parser.add_argument('--es_password', type=str, help='Elasticsearch password')

args = parser.parse_args()

# Parse the dates
start_date = datetime.strptime(args.start_date, "%Y-%m-%d")
end_date = datetime.strptime(args.end_date, "%Y-%m-%d")
domain = args.domain
index = args.index

# Elasticsearch configuration
es = Elasticsearch(
    [f"{args.es_scheme}://{args.es_host}:{args.es_port}"],
    http_auth=(args.es_username, args.es_password)
)

def process_article(article_data):
    """Process an individual GDELT article data and extract relevant fields."""
    try:
        url = article_data.get("url")
        article = Article(url)
        article.download()
        article.parse()
        published_date = article.publish_date

        if not published_date:
            soup = BeautifulSoup(article.html, 'html.parser')
            time_tag = soup.find('time')
            if time_tag:
                date_str = time_tag.get_text()
                try:
                    # Removing timezone abbreviation for parsing
                    date_str, _, tz_str = date_str.rpartition(' ')
                    date_obj = datetime.strptime(date_str.strip(), '%B %d, %Y %I:%M%p')

                    # Manually handling 'EDT' and 'EST' timezones
                    if tz_str == 'EDT':
                        tz = pytz.FixedOffset(-240)  # 'EDT' is UTC-4
                    elif tz_str == 'EST':
                        tz = pytz.FixedOffset(-300)  # 'EST' is UTC-5
                    else:
                        print(f"Unrecognized timezone: {tz_str}")
                        tz = pytz.UTC  # Default to UTC

                    date_obj = tz.localize(date_obj)

                    # Converting to UTC and printing the date
                    published_date = date_obj.astimezone(pytz.utc).strftime('%Y-%m-%dT%H:%M:%SZ')
                    print(f"Article published date: {published_date}")

                except ValueError as e:
                    print(f"Could not parse the date from the tag: {date_str}. Error: {e}")

        data = {
            "title": article.title,
            "text": article.text,
            "authors": article.authors,
            "published_date": published_date,
            "summary": article.summary,
            "url": url
        }
        es.index(index=index, document=data)  # Indexing to Elasticsearch
        return True
    except Exception as e:
        print(f"Error processing article at {url}: {e}")
        return False

def fetch_articles_from_gdelt(start_datetime, end_datetime):
    """Fetch articles from GDELT for a specified date range."""
    url = "http://api.gdeltproject.org/api/v2/doc/doc"
    params = {
        "query": f"domain:{domain}",
        "format": "json",
        "maxrecords": 250,
        "STARTDATETIME": start_datetime,
        "ENDDATETIME": end_datetime
    }
    response = requests.get(url, params=params)
    if response.status_code == 200:
        articles_data = json.loads(response.text)
        articles = articles_data.get("articles", [])
        total_articles = len(articles)
        print(f"Total articles: {total_articles}")
        indexed_count = 0
        for article in articles:
            if process_article(article):
                indexed_count += 1
                remaining_articles = total_articles - indexed_count
                print(f"Articles remaining: {remaining_articles}")
    else:
        print(f"Error fetching data. Status code: {response.status_code}")

def fetch_articles_for_date_range(start_date, end_date):
    """Iterate over each day in the specified date range and fetch articles."""
    current_date = start_date
    while current_date <= end_date:
        start_datetime = current_date.strftime("%Y%m%d%H%M%S")
        end_datetime = (current_date + timedelta(days=1)).strftime("%Y%m%d%H%M%S")
        fetch_articles_from_gdelt(start_datetime, end_datetime)
        current_date += timedelta(days=1)

# Run the function
fetch_articles_for_date_range(start_date, end_date)
