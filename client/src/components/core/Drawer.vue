<template>
  <v-navigation-drawer
    id="app-drawer"
    v-model="inputValue"
    src="/img/drawer-background.jpg"
    app
    color="grey darken-2"
    dark
    floating
    mobile-break-point="991"
    persistent
    width="260"
  >
    <template v-slot:img="attrs">
      <v-img
        v-bind="attrs"
        gradient="to top, rgba(0, 0, 0, .7), rgba(0, 0, 0, .7)"
      />
    </template>

    <v-list-item two-line>
      <v-list-item-avatar color="white">
        <v-img
          src="/img/logo.png"
          height="34"
          contain
        />
      </v-list-item-avatar>

      <v-list-item-title class="title">
        web-dev-tools
      </v-list-item-title>
    </v-list-item>

    <v-divider class="mx-3 mb-3" />

    <v-list nav>
      <!-- Bug in Vuetify for first child of v-list not receiving proper border-radius -->
      <div />

      <v-list-item
        v-for="(link, i) in links"
        :key="i"
        :to="link.to"
        active-class="primary white--text"
      >
        <v-list-item-action>
          <v-icon>{{ link.icon }}</v-icon>
        </v-list-item-action>

        <v-list-item-title v-text="link.text" />
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { appModule } from "@/store/modules/app";
@Component({
  components: {}
})
export default class Drawer extends Vue {
  links: object[] = [
    {
      to: "/",
      icon: "mdi-view-dashboard",
      text: "Dashboard"
    },
    {
      to: "/json-formatter",
      icon: "mdi-code-braces",
      text: "JSON Formatter"
    },
    {
      to: "/url-encoding",
      icon: "mdi-text",
      text: "URL Encoding"
    }
  ];

  get inputValue(): boolean {
    return appModule.drawer;
  }
  set inputValue(value: boolean) {
    appModule.setDrawer(value);
  }
}
</script>
