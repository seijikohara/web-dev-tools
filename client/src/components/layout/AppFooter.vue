<template>
  <div class="layout-footer">Seiji Kohara &copy; 2020</div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({});
</script>

<style lang="scss">
.layout-footer {
  display: flex;
  font-size: 1rem;
  margin-left: $sidebarWidth;
  padding: 1.5rem;
  background-color: var(--surface-a);
  align-items: center;
  justify-content: space-between;
}
@media screen and (max-width: $breakpoint) {
  .layout-footer {
    margin-left: 0;
  }
}
</style>
