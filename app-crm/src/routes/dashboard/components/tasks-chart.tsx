import React, { useState, useEffect, lazy, Suspense } from "react";
import axios from 'axios';
import { PieConfig } from "@ant-design/plots";
import { Card, Tooltip } from "antd";
import { ProjectOutlined } from "@ant-design/icons";
import { Text } from "@/components"; // Ensure correct import path
import configa from 'config/config'; // Adjust the path as necessary

const Pie = lazy(() => import("@ant-design/plots/es/components/pie"));

interface Task {
    title: string;
    value: number;
}
export const DashboardTasksChart: React.FC<{chart_title: string, indexAI: string, description: string}> = ({ chart_title, indexAI, description }) => {
    const [tasksData, setTasksData] = useState<Task[]>([]);
    const apiUrl = configa.API_URL;

    useEffect(() => {
        axios.get(`${apiUrl}api/news/openai-data?indexName=${indexAI}`)
            .then(response => {
                const transformedData = response.data
                    .map((item: { openAI: string, count: number }) => ({
                        title: item.openAI,
                        value: item.count
                    }))
                    .sort((a: Task, b: Task) => b.value - a.value);
                setTasksData(transformedData);
            })
            .catch(error => console.error("Error fetching task chart data", error));
    }, [indexAI]);

    const COLORS = [
        "#BAE0FF", "#69B1FF", "#1677FF", "#0958D9", "#10239E",
        "#061178", "#030852", "#03052E", "#000B0A", "#000000",
    ];

    const config: PieConfig = {
        padding: [10, 20, 50, 20],
        data: tasksData,
        angleField: "value",
        colorField: "title",
        color: COLORS,
        legend: false,
        radius: 1,
        innerRadius: 0.4,
        label: {
            type: 'outer',
            content: ({ title, value }, _, index) => index < 5 ? `${title}: ${value}` : '',
        },
        interactions: [
            { type: 'pie-legend-active' },
            { type: 'element-active' },
        ],
        statistic: {
            title: false,
            content: false,
        },
    };

    return (
        <Card
            style={{ display: 'flex', flexDirection: 'column', width: '100%', overflow: 'hidden' }}
            bodyStyle={{ display: 'flex', flexDirection: 'row', padding: '20px', alignItems: 'flex-start', justifyContent: 'center', gap: '20px' }}
            title={
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <ProjectOutlined />
                    <Tooltip title={description}>
                        <Text size="sm" style={{ cursor: 'pointer' }}>{chart_title}</Text>
                    </Tooltip>
                </div>
            }
        >
            <Suspense fallback={<div>Loading...</div>}>
                <div style={{ width: '60%', minHeight: '360px' }}>
                    <Pie {...config} />
                </div>
            </Suspense>
            <div style={{ width: '40%', maxHeight: '360px', overflowY: 'auto' }}>
            {tasksData.slice(0, 10).map((item, index) => (
                    <div key={index} style={{ display: "flex", alignItems: "center", marginBottom: "8px" }}>
                        <div style={{
                            width: 18,
                            height: 18,
                            backgroundColor: COLORS[index % COLORS.length],
                            marginRight: "10px",
                        }} />
                        <Text size="md" style={{ textTransform: "capitalize" }}>{item.title}</Text>
                    </div>
                ))}
            </div>
        </Card>
    );
};
