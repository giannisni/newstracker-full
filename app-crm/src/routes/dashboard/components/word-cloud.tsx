import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { WordCloud, WordCloudConfig } from '@ant-design/plots';
import { Card, Tooltip } from "antd";
import configa from 'config/config'; // Adjust the path as necessary

// Define the structure for the word data
interface WordData {
    text: string;
    value: number;
}

export const DashBoardWordCloudChart = () => {
    const [data, setData] = useState([]);

    useEffect(() => {
        const fetchWordCloudData = async() => {
            try {
            const apiUrl = configa.API_URL;
            const response = await axios.get(`${apiUrl}api/news/wordcloud?indexName=cnn_keyword_frequencies`);
            const transformedData = response.data.map((word: WordData) => ({
            x: word.text, // 'x' as the word field
            value: word.value * 10000 // Adjust size scaling as needed
            }));
            setData(transformedData);
            } catch (error) {
            console.error("Error fetching word cloud data:", error);
            }
            };
            fetchWordCloudData();
            }, []);
            const config: WordCloudConfig = {
                data,
                wordField: 'x',
                weightField: 'value',
                color: '#122c6a',
                wordStyle: {
                    fontFamily: 'Verdana',
                    fontSize: [24, 80] as [number, number], // Correct type for fontSize
                },
                interactions: [{ type: 'element-active' }],
                state: {
                    active: { style: { lineWidth: 3 } },
                },
            };
            
            return (
                <Card 
                    style={{ height: '100%', padding: '10px' }} 
                    title={
                        <Tooltip title="Word cloud about keywords extracted from documents about Israel-Palestine conflict">
                            <span style={{ cursor: 'pointer' }}>Word Cloud</span>
                        </Tooltip>
                        }
                        >
                        <WordCloud {...config} />
                        </Card>
                        );
                        };
            
