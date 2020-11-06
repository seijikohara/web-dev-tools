<template>
  <div class="loading-container">
    <ProgressSpinner />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

import ProgressSpinner from "primevue/progressspinner";

export default defineComponent({
  name: "LoadingSpinner",
  components: { ProgressSpinner },
});
</script>

<style lang="scss" scoped>
.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}
</style>
