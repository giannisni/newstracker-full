import React, { useState, useEffect, Suspense } from "react";
import axios from 'axios';
import { GaugeConfig } from "@ant-design/plots";
import { Card } from "antd";
import { Text } from "@/components"; // Ensure correct import path
import configa from 'config/config'; // Adjust the path as necessary

const Gauge = React.lazy(() => import("@ant-design/plots/es/components/gauge"));

export const DashboardSentimentAnalysisChart: React.FC<{
    chart_title: string;
    description?: string;
    term: string;
    index: string;
    sentiment_index: string;
}> = ({ chart_title, description, sentiment_index, term, index }) => {
    const apiUrl = configa.API_URL;
    const [sentimentScore, setSentimentScore] = useState<number>(0);

    useEffect(() => {
        const fetchSentimentScore = async () => {
            const startDate = "2023-01-01";
            const endDate = "2023-12-31";
            try {
                const response = await axios.get(`${apiUrl}api/news/calculate?`, {
                    params: {
                        index,
                        sentiment_index: `${sentiment_index}_articles_new`,
                        term,
                        startDate,
                        endDate
                    }
                });
                if (response.data && typeof response.data === 'number') {
                    setSentimentScore(response.data);
                }
            } catch (error) {
                console.error('Error fetching sentiment score:', error);
            }
        };
        fetchSentimentScore();
    }, [sentiment_index, index, term, apiUrl]);

    const config: GaugeConfig = {
        percent: (sentimentScore + 1) / 2, // Normalize score for gauge
        range: {
            color: 'l(0) 0:#FF4D4F 0.5:#FAAD14 1:#52C41A', // Gradient from red to green
        },
        axis: {
            label: {
                formatter: (value) => `${((+value) * 2 - 1).toFixed(2)}`, // Using unary plus operator
            },
        },
        statistic: {
            content: {
                formatter: () => `${sentimentScore.toFixed(2)}`,
                style: {
                    color: 'rgba(0,0,0,0.85)',
                    fontWeight: 500,
                    fontSize: '24px',
                },
            },
        },
    };

    return (
        <Card
            style={{ width: '100%', maxWidth: '800px', margin: '0 auto' }} // Make the card width responsive
            bodyStyle={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%' }}
            title={<Text size="sm">{chart_title}</Text>}
        >
            <Suspense fallback={<div>Loading Gauge...</div>}>
                <Gauge {...config} style={{ width: '100%', height: 'auto' }} />
            </Suspense>
            {description && <Text size="xs" style={{ marginTop: '10px' }}>{description}</Text>}
        </Card>
    );
};
