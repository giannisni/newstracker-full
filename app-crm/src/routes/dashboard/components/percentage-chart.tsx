import React, { useState, useEffect, lazy, Suspense } from "react";
import axios from 'axios';
import { PieConfig } from "@ant-design/plots";
import { Card, Tooltip } from "antd";
import configa from 'config/config'; // Adjust the path as necessary
const Pie = lazy(() => import("@ant-design/plots/es/components/pie"));

// Define a type for the data in termsData state
type TermData = {
  title: string;
  value: any; // Consider specifying a more specific type than 'any' if possible
};

export const DashboardTermsPercentageChart: React.FC<{index_name:String,keyword1: String, keyword2: String, keyword3: String, chartTitle: String}> = ({index_name,keyword1, keyword2, keyword3, chartTitle}) => {
    // Initialize termsData with the correct type
    const [termsData, setTermsData] = useState<TermData[]>([]);
    const apiUrl = configa.API_URL;
    // const elastic_index = configa.INDEX_NAME;

    useEffect(() => {
        axios.get(`${apiUrl}api/news/term-percentages`, {
            params: {
                term1: keyword1,
                term2: keyword3,
                term3: keyword2,
                term4: keyword3,
                indexName: `${index_name}_articles_newone`,
                startDate: '2023-01-01',
                endDate: '2024-12-31'
            }
        })
        .then(response => {
            const transformedData: TermData[] = Object.keys(response.data).map(key => ({
                title: key,
                value: response.data[key]
            }));
            setTermsData(transformedData);
        })
        .catch(error => console.error("Error fetching term percentages", error));
    }, [index_name, keyword1, keyword2, keyword3]); // Include index_name in the dependency array

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
            style={{ height: "100%", width: "100%" }}
            title={
                <Tooltip title="Words in relation to genocide term">
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
