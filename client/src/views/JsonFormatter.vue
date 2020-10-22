<template>
  <Card>
    <template #title>
      JSON Formatter
    </template>
    <template #subtitle>
      Formatting JSON
    </template>
    <template #content>
      <Editor v-model:value="state.content" mode="json" height="500px" />
    </template>
    <template #footer>
      <div class="p-inputgroup">
        <Button label="Format" @click="onClickFormat" />
        <Dropdown
          v-model="state.formatOptionValue"
          :options="formatOptions"
          optionLabel="text"
          optionValue="value"
        />
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent, reactive, readonly } from "vue";

import Button from "primevue/button";
import Card from "primevue/card";
import Dropdown from "primevue/dropdown";

import Editor from "@/components/Editor.vue";

type FormatOption = {
  text: string;
  value: string;
};

export default defineComponent({
  components: { Button, Card, Dropdown, Editor },
  setup() {
    const formatOptions = readonly([
      { text: "2 Spaces", value: " ".repeat(2) },
      { text: "4 Spaces", value: " ".repeat(4) },
      { text: "1 Tab", value: "\t" },
      { text: "Compact", value: "" }
    ] as FormatOption[]);
    const state = reactive({
      content: "{}",
      formatOptionValue: formatOptions[0].value
    });
    const onClickFormat = () => {
      const parsed = JSON.parse(state.content);
      const padString = state.formatOptionValue;
      state.content = JSON.stringify(parsed, undefined, padString);
    };
    return {
      formatOptions,
      state,
      onClickFormat
    };
  }
});
</script>

<style lang="scss" scoped>
.buttons {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
}
</style>
