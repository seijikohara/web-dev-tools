<template>
  <div class="layout-content">
    <div class="content-section">
      <router-view v-slot="{ Component }">
        <Suspense timeout="0">
          <template #default>
            <component :is="Component" />
          </template>
          <template #fallback>
            <LoadingSpinner />
          </template>
        </Suspense>
      </router-view>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

import LoadingSpinner from "@/components/LoadingSpinner.vue";

export default defineComponent({
  components: { LoadingSpinner }
});
</script>

<style lang="scss" scoped>
.layout-content {
  margin-left: $sidebarWidth;
  padding-top: $topbarHeight;
  .content-section {
    padding: 2rem;
    background-color: var(--surface-b);
    color: var(--text-color);
  }
}
@media screen and (max-width: $breakpoint) {
  .layout-content {
    margin-left: 0;
  }
}
</style>
