import{r as o,b as c}from"./antd-cbf28044.js";import{b as d,P as y,g as b,E as v,C as T}from"./index-aac9b36e.js";var O=globalThis&&globalThis.__rest||function(e,n){var a={};for(var t in e)Object.prototype.hasOwnProperty.call(e,t)&&n.indexOf(t)<0&&(a[t]=e[t]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var r=0,t=Object.getOwnPropertySymbols(e);r<t.length;r++)n.indexOf(t[r])<0&&Object.prototype.propertyIsEnumerable.call(e,t[r])&&(a[t[r]]=e[t[r]]);return a},P=o.forwardRef(function(e,n){var a=e.chartRef,t=e.style,r=t===void 0?{height:"inherit"}:t,f=e.className,m=e.loading,u=e.loadingTemplate,s=e.errorTemplate,h=O(e,["chartRef","style","className","loading","loadingTemplate","errorTemplate"]),i=d(y,h),l=i.chart,g=i.container;return o.useEffect(function(){b(a,l.current)},[l.current]),o.useImperativeHandle(n,function(){return{getChart:function(){return l.current}}}),c.createElement(v,{errorTemplate:s},m&&c.createElement(T,{loadingTemplate:u,theme:e.theme}),c.createElement("div",{className:f,style:r,ref:g}))});export{P as default};
