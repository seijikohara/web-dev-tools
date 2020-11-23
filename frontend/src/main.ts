import { createApp } from "vue";
import VueGtag from "vue-gtag-next";
import App from "./App.vue";
import { store } from "./store";
import router from "./router";

import "@/assets/scss/layout.scss";

const app = createApp(App);
app.use(store);
app.use(router);

const gaMeasurementId = process.env.VUE_APP_GA_MEASUREMENT_ID;
if (gaMeasurementId) {
  app.use(VueGtag, {
    property: {
      id: gaMeasurementId,
    },
  });
}

app.mount("#app");

app.config.globalProperties.$primevue = { ripple: true };
