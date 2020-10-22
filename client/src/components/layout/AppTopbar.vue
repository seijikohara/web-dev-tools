<template>
  <div class="layout-topbar">
    <a class="menu-button" @click="onClickMenu"><i class="pi pi-bars"/></a>
    <router-link to="/">
      <img src="/img/logo-top.png" height="60" />
    </router-link>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useStore } from "vuex";

import { ActionTypes } from "@/store";

export default defineComponent({
  setup() {
    const store = useStore();
    const onClickMenu = () => store.dispatch(ActionTypes.SET_DRAWER, true);
    return {
      onClickMenu
    };
  }
});
</script>

<style lang="scss">
.layout-topbar {
  background-color: #59606d;
  height: $topbarHeight;
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  z-index: 997;
  box-shadow: 0 0 5px rgba(89, 96, 109, 0.5);
  border-bottom: 1px solid var(--surface-d);
  display: flex;
  align-items: center;
  padding: 0 35px;
  .menu-button {
    display: none;
    color: var(--primary-color-text);
    width: 70px;
    height: $topbarHeight;
    line-height: 70px;
    text-align: center;
    transition: background-color $transitionDuration;
    cursor: pointer;
  }
}
@media screen and (max-width: $breakpoint) {
  .layout-topbar {
    flex-wrap: wrap;
    padding: 0;
    .menu-button {
      display: block;
    }
  }
}
</style>
