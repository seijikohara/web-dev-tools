<template>
  <div class="layout-content">
    <div class="content-section">
      <router-view v-slot="{ Component }">
        <Suspense>
          <template #default>
            <component :is="Component" />
          </template>
          <template #fallback>
            <span>Loading...</span>
          </template>
        </Suspense>
      </router-view>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({});
</script>

<style lang="scss">
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
