import{r as o,b as c}from"./antd-4ead0eed.js";import{b as d,G as y,g as b,E as v,C as T}from"./index-2913fdaa.js";var O=globalThis&&globalThis.__rest||function(e,n){var a={};for(var t in e)Object.prototype.hasOwnProperty.call(e,t)&&n.indexOf(t)<0&&(a[t]=e[t]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var r=0,t=Object.getOwnPropertySymbols(e);r<t.length;r++)n.indexOf(t[r])<0&&Object.prototype.propertyIsEnumerable.call(e,t[r])&&(a[t[r]]=e[t[r]]);return a},_=o.forwardRef(function(e,n){var a=e.chartRef,t=e.style,r=t===void 0?{height:"inherit"}:t,f=e.className,m=e.loading,u=e.loadingTemplate,s=e.errorTemplate,g=O(e,["chartRef","style","className","loading","loadingTemplate","errorTemplate"]),i=d(y,g),l=i.chart,h=i.container;return o.useEffect(function(){b(a,l.current)},[l.current]),o.useImperativeHandle(n,function(){return{getChart:function(){return l.current}}}),c.createElement(v,{errorTemplate:s},m&&c.createElement(T,{loadingTemplate:u,theme:e.theme}),c.createElement("div",{className:f,style:r,ref:h}))});export{_ as default};
