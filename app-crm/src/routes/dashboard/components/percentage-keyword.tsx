import React, { useState, useEffect, lazy, Suspense } from "react";
import axios from 'axios';
import { PieConfig } from "@ant-design/plots";
import { Card, Tooltip } from "antd";
import configa from 'config/config'; // Adjust the path as necessary
const Pie = lazy(() => import("@ant-design/plots/es/components/pie"));

// Define the data structure for the terms data
interface TermData {
    title: string;
    value: unknown; // Replace with the appropriate type
}

export const DashboardTermKeywordPercentageChart: React.FC<{ keyword1: string, keyword2: string, chartTitle: string }> = ({ keyword1, keyword2, chartTitle }) => {
    const [termsData, setTermsData] = useState<TermData[]>([]);
    const apiUrl = configa.API_URL;

    useEffect(() => {
        axios.get(`${apiUrl}api/news/keyword-percentages`, {
            params: {
                keyword1: keyword1,
                keyword2: keyword2,
                indexName: 'cnn_articles_newone' // Adjust the index name as necessary
            }
        })
        .then(response => {
            const transformedData = Object.entries(response.data).map(([key, value]) => ({
                title: key,
                value: value
            }));
            setTermsData(transformedData);
        })
        .catch(error => console.error("Error fetching keyword percentages", error));
    }, [keyword1, keyword2]);

    const config: PieConfig = {
        data: termsData,
        angleField: "value",
        colorField: "title",
        radius: 1,
        innerRadius: 0.4,
        label: {
            type: 'outer',
            content: '{name}: {percentage}',
        },
        interactions: [
            {
                type: 'pie-legend-active',
            },
            {
                type: 'element-active',
            },
        ],
        statistic: {
            title: false,
            content: false,
        },
    };

    return (
        <Card
            style={{ height: "100%", width:"100%" }}
            title={
                <Tooltip title="Israel-Palestine keyword ratio">
                    <span style={{ cursor: 'pointer' }}>{chartTitle}</span>
                </Tooltip>
            }
        >
            <Suspense fallback={<div>Loading...</div>}>
                <Pie {...config} />
            </Suspense>
        </Card>
    );
};
