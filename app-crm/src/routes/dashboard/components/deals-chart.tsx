import React, { useState, useEffect, lazy, Suspense } from "react";
import axios from 'axios';
import { AreaConfig } from "@ant-design/plots";
import { Button, Card } from "antd";
import { DollarOutlined, RightCircleOutlined } from "@ant-design/icons";
import { Text } from "@/components";
import configa from 'config/config'; // Adjust the path as necessary
import { Tooltip } from 'antd';

const Area = lazy(() => import("@ant-design/plots/es/components/area"));

interface DealDataType {
    timeText: string;
    value: number;
}
export const DashboardDealsChart: React.FC<{ selectedKeyword: string,chartTitle:string }> = ({ selectedKeyword,chartTitle}) => {
    const [dealData, setDealData] = useState<DealDataType[]>([]);
    const apiUrl = configa.API_URL;

    console.log("word: ",selectedKeyword)
    useEffect(() => {
        const fetchData = async () => {
            const startDate = new Date('2023-10-01').toISOString().split('T')[0];
            const endDate = new Date('2023-12-28').toISOString().split('T')[0];
            try {
                const keyword = selectedKeyword ; // Replace 'DefaultKeyword' with a default value
                const response = await axios.get(`http://localhost:8080/api/news/counts?index=cnn_articles_newone&keyword=${keyword}&startDate=${startDate}&endDate=${endDate}`);
                const transformedData = Object.entries(response.data)
                    .map(([date, value]) => ({ timeText: date, value: Number(value) }))
                    .sort((a, b) => new Date(a.timeText).getTime() - new Date(b.timeText).getTime());
                    setDealData(transformedData);
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };
        fetchData();
    }, [selectedKeyword]);

    const max_value = Math.max(...dealData.map(item => item.value)); // Calculate the maximum value

    const config: AreaConfig = {
        isStack: false,
        data: dealData,
        xField: "timeText",
        yField: "value",
        meta: {
            value: {
                min: 0,
                max: 900,
                // You might also need to set 'nice' to false to ensure that the chart doesn't adjust the max value on its own
                nice: false,
            },
            timeText: {
                type: 'timeCat',
                range: [0, 1], // This ensures that the scale goes from the minimum to the maximum value of the time axis
            }
        }
        // other chart configurations
    };

    return (
        <Card
        style={{ height: "450px", width:"100%"}}
        headStyle={{ padding: "8px 16px" }}
        bodyStyle={{ padding: "24px 24px 0px 24px" }}
        title={
        <Tooltip title="Keyword frequencies in relation">
            <span style={{ cursor: 'pointer' }}>{chartTitle}</span>
        </Tooltip>
        }
    >
        <Suspense fallback={<div>Loading...</div>}>
            <Area {...config} height={325} />
        </Suspense>
    </Card>
    );
};
