<template>
  <Card>
    <template #title> JSON to TypeScript </template>
    <template #subtitle> Convert JSON to TypeScript interface </template>
    <template #content>
      <div class="p-grid">
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="JSON">
            <Editor v-model:value="state.json" mode="json" height="500px" />
          </Panel>
        </div>
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="TypeScript interface">
            <Editor
              v-model:value="state.typeScript"
              mode="typescript"
              height="500px"
            />
          </Panel>
        </div>
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent, reactive, watch } from "vue";
import JsonToTS from "json-to-ts";

import Card from "primevue/card";
import Panel from "primevue/panel";

import Editor from "@/components/Editor.vue";

export default defineComponent({
  components: { Card, Editor, Panel },
  async setup() {
    const state = reactive({
      json: "{}",
      typeScript: "",
    });

    watch(
      () => state.json,
      (json: string) => {
        const jsonToObject = (json: string): unknown => {
          try {
            return JSON.parse(json);
          } catch (e) {
            return {};
          }
        };

        const object = jsonToObject(json);
        state.typeScript = JsonToTS(object).join("\r\n\r\n");
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
