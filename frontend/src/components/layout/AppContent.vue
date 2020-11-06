<template>
  <div class="layout-content">
    <div class="content-section">
      <router-view v-slot="{ Component }">
        <Message v-if="error" severity="error" :closable="false">
          {{ error.message }}
        </Message>
        <Suspense v-else timeout="0">
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
import { defineComponent, onErrorCaptured, Ref, ref, watch } from "vue";
import { useRoute } from "vue-router";

import Message from "primevue/message";

import LoadingSpinner from "@/components/LoadingSpinner.vue";

export default defineComponent({
  components: { Message, LoadingSpinner },
  setup() {
    const error: Ref<unknown> = ref(null);
    onErrorCaptured((e) => {
      error.value = e;
      return true;
    });
    const route = useRoute();
    watch(
      () => route.params,
      () => (error.value = null)
    );
    return {
      error,
    };
  },
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
