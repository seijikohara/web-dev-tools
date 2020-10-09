import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/",
      name: "dashboard",
      component: () => import("./views/Dashboard.vue")
    },
    {
      path: "/json-formatter",
      name: "json formatter",
      component: () => import("./views/JsonFormatter.vue")
    },
    {
      path: "/url-encoding",
      name: "url encoding",
      component: () => import("./views/UrlEncoding.vue")
    },
    {
      path: "/hash",
      name: "hash",
      component: () => import("./views/HashGenerator.vue")
    },
    {
      path: "/b-crypt",
      name: "bcrypt",
      component: () => import("./views/BCryptHashCalculator.vue")
    }
  ]
});
