import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Tooltip, Card } from 'antd';
import dayjs from 'dayjs';
import configa from 'config/config'; // Adjust the path as necessary

interface Highlight {
    content: string;
}
interface Highlights {
    [documentId: string]: Highlight[];
}

export const DashboardHighlights: React.FC<{keyword1: string, keyword2: string, chartTitle: string}> = ({keyword1, keyword2, chartTitle}) => {
    const [activities, setActivities] = useState([]); // If you need to type this, add the type similarly
    const [highlights, setHighlights] = useState<Highlights>({});
    const [isLoading, setIsLoading] = useState(true);
    const apiUrl = configa.API_URL;

    useEffect(() => {
        axios.get(`${apiUrl}api/news/highlights`, {
            params: {
                term1: keyword1,
                term2: keyword2,
                indexName: 'fox_articles_new',
                startDate: '2023-01-01',
                endDate: '2023-12-31'
            }
        })
        .then(response => {
            setHighlights(response.data);
        })
        .catch(error => {
            console.error('Error fetching highlights:', error);
        })
        .finally(() => {
            setIsLoading(false);
        });
    }, [keyword1, keyword2]); // Added keyword1 and keyword2 as dependencies

    const renderHighlights = (documentId: string) => {
        return highlights[documentId]?.slice(0, 4).map((highlight, index: number) => (
            <p key={index} dangerouslySetInnerHTML={{ __html: highlight }} style={{ marginBottom: '10px' }} />
        ));
    };

    return (
        <div style={{ width: '100%', margin: "0px" }}>
            <style>
                {`
                    em {
                        font-weight: bold;
                    }
                `}
            </style>
            <Card 
                title={
                    <Tooltip title="Highlighted text in the context of the keywords searched">
                        <span style={{ cursor: 'pointer' }}>{chartTitle}</span>
                    </Tooltip>
                }
                loading={isLoading}
            >
                <div>
                    {Object.keys(highlights).slice(0, 7).map((documentId, index: number) => (
                        <div key={index} style={{ marginBottom: '30px' }}>
                            {renderHighlights(documentId)}
                        </div>
                    ))}
                </div>
            </Card>
        </div>
    );
};