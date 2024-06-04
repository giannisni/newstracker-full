import requests
import json
from newspaper import Article
from elasticsearch import Elasticsearch
from datetime import datetime, timedelta
import argparse  # Ensure this import statement is at the top of your script


# Set up command-line argument parsing
parser = argparse.ArgumentParser(description='Process some articles.')
parser.add_argument('--start_date', type=str, help='Start date in YYYY-MM-DD format')
parser.add_argument('--end_date', type=str, help='End date in YYYY-MM-DD format')
parser.add_argument('--domain', type=str, default='cnn.com', help='Domain to query for articles')
parser.add_argument('--index', type=str, default='', help='Index ')


args = parser.parse_args()

# Parse the dates
start_date = datetime.strptime(args.start_date, "%Y-%m-%d")
end_date = datetime.strptime(args.end_date, "%Y-%m-%d")
domain = args.domain
index= args.index
# Elasticsearch configuration
es = Elasticsearch([{'host': "localhost", 'port': 9200, 'scheme': 'http'}])

def process_article(article_data):
    """Process an individual GDELT article data and extract relevant fields."""
    try:
        url = article_data.get("url")
        article = Article(url)
        article.download()
        article.parse()
        data = {
            "title": article.title,
            "text": article.text,
            "authors": article.authors,
            "published_date": article.publish_date,
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
        # Preprocess the response to escape backslashes before decoding
        corrected_response_text = response.text.replace('\\', '\\\\')
        try:
            articles_data = json.loads(corrected_response_text)
            articles = articles_data.get("articles", [])
            total_articles = len(articles)
            print(f"Total articles: {total_articles}")
            indexed_count = 0
            for article in articles:
                if process_article(article):
                    indexed_count += 1
                    remaining_articles = total_articles - indexed_count
                    print(f"Articles remaining: {remaining_articles}")
        except json.JSONDecodeError as e:
            print(f"Error decoding JSON: {e}")
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

# Define your start and end dates here
# start_date = datetime(2024, 3, 11)  # Start date: October 1, 2023
# end_date = datetime(2024, 6, 3)    # End date: December 1, 2023

# Run the function
fetch_articles_for_date_range(start_date, end_date)
