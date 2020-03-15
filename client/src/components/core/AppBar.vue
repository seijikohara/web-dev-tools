<template>
  <v-app-bar
    id="core-app-bar"
    absolute
    app
    color="transparent"
    flat
    height="30"
  >
    <v-toolbar-title class="tertiary--text font-weight-light align-self-center">
      <v-btn v-if="responsive" dark icon @click.stop="onClick">
        <v-icon>mdi-view-list</v-icon>
      </v-btn>
      {{ title }}
    </v-toolbar-title>
    <v-spacer />
  </v-app-bar>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { appModule } from "@/store/modules/app";
@Component({
  components: {}
})
export default class AppBar extends Vue {
  title = "";
  responsive = false;

  // lifescycle hook
  mounted(): void {
    window.addEventListener("resize", this.onResponsiveInverted);
    this.onResponsiveInverted();
    appModule.setDrawer(!this.responsive);
  }
  beforeDestroy(): void {
    window.removeEventListener("resize", this.onResponsiveInverted);
  }

  // method
  onClick(): void {
    appModule.toggleDrawer();
  }
  onResponsiveInverted(): void {
    this.responsive = window.innerWidth < 991;
  }
}
</script>
