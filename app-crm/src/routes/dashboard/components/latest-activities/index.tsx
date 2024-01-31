import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Tooltip, Card } from 'antd';
import dayjs from 'dayjs';
import configa from 'config/config'; // Adjust the path as necessary

// Define the structure of an activity
interface Activity {
    title: string;
    date: string;
    url: string;
}

export const DashboardLatestActivities : React.FC<{index_name:String }> = ({ index_name}) => {
    const [activities, setActivities] = useState<Activity[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const apiUrl = configa.API_URL;

    console.log(`${index_name}_articles_new_news_topics`);
    useEffect(() => {
        axios.get(`${apiUrl}api/news/by-topic`, {
            params: {
                
                index: `${index_name}_articles_new_news_topics`,
                topicId: '3',
                startDate: '2023-01-01',
                endDate: '2023-12-31'
            }
        })
        .then(response => {
            const transformedData = response.data.map((doc: Activity) => ({
                title: doc.title,
                date: doc.date,
                url: doc.url
            }))
            .sort((a: Activity, b: Activity) => new Date(b.date).getTime() - new Date(a.date).getTime()) // Sort by date, latest first
            .slice(0, 10); // Limit to 10 results
            console.log('Transformed Data:', transformedData); // Log transformed data
            setActivities(transformedData);
        })
        .catch(error => {
            console.error('Error fetching activities:', error);
            // Optionally log additional error details here
        })
        .finally(() => {
            setIsLoading(false);
            console.log('Final Activities State:', activities); // Log final state of activities
        });
    }, [index_name]);

    return (
        <Card 
        title={
            <Tooltip title="Latest news about Israel-Palestine conflict">
                <span style={{ cursor: 'pointer' }}>Latest Activities</span>
            </Tooltip>
        }
        loading={isLoading}
        style={{ height: "100%", width:"74%"}}
    >
        <div>
            {activities.map((activity, index) => (
                <div key={index}>
                    <h3>
                        <a href={activity.url} target="_blank" rel="noopener noreferrer">
                            {activity.title}
                        </a>
                    </h3>
                    <p>{dayjs(activity.date).format('MMMM D, YYYY')}</p>
                </div>
            ))}
        </div>
    </Card>
    );
};
