<template>
  <div class="layout-footer">Seiji Kohara &copy; {{ year }}</div>
</template>

<script lang="ts">
import { computed, defineComponent } from "vue";
import dayjs from "dayjs";

export default defineComponent({
  setup() {
    return {
      year: computed(() => dayjs().format("YYYY")),
    };
  },
});
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
