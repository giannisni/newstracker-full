import React, { Suspense } from "react";
import { GaugeConfig } from "@ant-design/plots";
import { Card } from "antd";
import { Text } from "@/components"; // Ensure this path is correct

const Gauge = React.lazy(() => import("@ant-design/plots/es/components/gauge"));

export const DashboardSentimentAnalysisChart: React.FC<{
    sentimentScore: number;
    chart_title: string;
    description?: string; // Optional description prop
}> = ({ sentimentScore, chart_title, description }) => {
    // Ensure sentimentScore is a number and within expected range
    const validSentimentScore = typeof sentimentScore === 'number' ? sentimentScore : 0;
    const normalizedScore = (Number(validSentimentScore as any) + 1) / 2;

    const config: GaugeConfig = {
        animation: true,
        supportCSSTransform: true,
        percent: normalizedScore, // Use the normalized score
        range: {
            color: "l(0) 0:#FF4D4F 0.5:#FAAD14 1:#52C41A", // Color gradient from red to green
        },
        axis: {
            label: {
                formatter: (v: any) => {
                    // Ensure v is a number before using it in arithmetic operation
                    const value = Number(v);
                    return ((value * 2 - 1).toFixed(2)); // Adjust labels to show range from -1 to +1
                },
            },
        },        
        statistic: {
            content: {
                formatter: () => {
                    return `${(sentimentScore).toFixed(2)}`; // Display the sentiment score
                },
                style: {
                    color: "rgba(0,0,0,0.85)",
                    fontWeight: 500,
                    fontSize: "24px",
                },
            },
        },
    };

    return (
        <Card
            style={{ height: "600px", width:"815px" }}
            bodyStyle={{
                padding: "22px",
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                alignItems: "center",
            }}
            title={
                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                    }}
                >
                    <Text size="sm">{chart_title}</Text>
                </div>
            }
        >
            <Suspense fallback={<div>Loading Gauge...</div>}>
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                    <Gauge {...config} padding={0} width={480} height={400} />
                </div>
            </Suspense>
            {description && (
                <Text size="xs" style={{ marginTop: '10px' }}>{description}</Text>
            )}
        </Card>
    );
};
