import {
  createRouter,
  createWebHashHistory,
  createWebHistory,
  RouteRecordRaw
} from "vue-router";

import { eq, when } from "@/helper/Switch";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/Dashboard.vue")
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: () => import("@/views/Dashboard.vue"),
    meta: {
      menu: true
    }
  },
  {
    path: "/json-formatter",
    name: "JSON Formatter",
    component: () => import("@/views/JsonFormatter.vue"),
    meta: {
      menu: true
    }
  },
  {
    path: "/xml-formatter",
    name: "XML Formatter",
    component: () => import("@/views/XmlFormatter.vue"),
    meta: {
      menu: true
    }
  },
  {
    path: "/url-encoding",
    name: "URL Encoding",
    component: () => import("@/views/UrlEncoding.vue"),
    meta: {
      menu: true
    }
  },
  {
    path: "/hash",
    name: "Hash",
    component: () => import("@/views/HashCalculator.vue"),
    meta: {
      menu: true
    }
  },
  {
    path: "/b-crypt",
    name: "BCrypt",
    component: () => import("@/views/BCryptHashCalculator.vue"),
    meta: {
      menu: true
    }
  }
];

const history = when(process.env.VUE_APP_HISTORY_MODE)
  .on(eq("history"), () => createWebHistory(process.env.BASE_URL))
  .on(eq("hash"), () => createWebHashHistory())
  .otherwise(() => createWebHistory(process.env.BASE_URL));

const scrollBehavior = () => {
  return {
    left: 0,
    top: 0
  };
};

const router = createRouter({
  history,
  routes,
  scrollBehavior
});

export default router;
