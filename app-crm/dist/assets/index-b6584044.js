import{r as o,b as c}from"./antd-30c2b566.js";import{b as g,d as y,g as b,E as v,C as T}from"./index-72bdb1a0.js";var O=globalThis&&globalThis.__rest||function(e,n){var a={};for(var r in e)Object.prototype.hasOwnProperty.call(e,r)&&n.indexOf(r)<0&&(a[r]=e[r]);if(e!=null&&typeof Object.getOwnPropertySymbols=="function")for(var t=0,r=Object.getOwnPropertySymbols(e);t<r.length;t++)n.indexOf(r[t])<0&&Object.prototype.propertyIsEnumerable.call(e,r[t])&&(a[r[t]]=e[r[t]]);return a},_=o.forwardRef(function(e,n){var a=e.chartRef,r=e.style,t=r===void 0?{height:"inherit"}:r,f=e.className,m=e.loading,u=e.loadingTemplate,s=e.errorTemplate,h=O(e,["chartRef","style","className","loading","loadingTemplate","errorTemplate"]),i=g(y,h),l=i.chart,d=i.container;return o.useEffect(function(){b(a,l.current)},[l.current]),o.useImperativeHandle(n,function(){return{getChart:function(){return l.current}}}),c.createElement(v,{errorTemplate:s},m&&c.createElement(T,{loadingTemplate:u,theme:e.theme}),c.createElement("div",{className:f,style:t,ref:d}))});export{_ as default};
