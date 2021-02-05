<template>
  <Card>
    <template #title> Markdown Editor </template>
    <template #subtitle> Editor and preview </template>
    <template #content>
      <div class="p-grid">
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="Markdown">
            <Editor
              v-model:value="state.markdown"
              mode="markdown"
              height="500px"
            />
          </Panel>
        </div>
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="Preview">
            <div v-html="state.html" />
          </Panel>
        </div>
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent, reactive, watch } from "vue";
import marked from "marked";
import DOMPurify from "dompurify";

import Card from "primevue/card";
import Panel from "primevue/panel";

import Editor from "@/components/Editor.vue";

export default defineComponent({
  components: { Card, Editor, Panel },
  async setup() {
    const state = reactive({
      markdown: "",
      html: "",
    });

    watch(
      () => state.markdown,
      (markdown: string) => {
        state.html = DOMPurify.sanitize(marked(markdown));
      }
    );
    return {
      state,
    };
  },
});
</script>

<style lang="scss" scoped>
.buttons {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
}
</style>
