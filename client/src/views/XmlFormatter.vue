<template>
  <Card>
    <template #title>
      XML Formatter
    </template>
    <template #subtitle>
      Formatting XML
    </template>
    <template #content>
      <Editor v-model:value="state.content" mode="xml" height="500px" />
    </template>
    <template #footer>
      <h3>Options</h3>
      <div class="p-field-checkbox">
        <Checkbox
          id="collapseContent"
          v-model="state.collapseContent"
          v-tooltip.right="
            'True to keep content in the same line as the element. Only works if element contains at least one text node'
          "
          :binary="true"
        />
        <label for="collapseContent">Collapse</label>
      </div>
      <div class="p-field-checkbox">
        <Checkbox
          id="whiteSpaceAtEndOfSelfclosingTag"
          v-model="state.whiteSpaceAtEndOfSelfclosingTag"
          v-tooltip.right="
            'To either end ad self closing tag with <tag/> or <tag />'
          "
          :binary="true"
        />
        <label for="whiteSpaceAtEndOfSelfclosingTag"
          >Self closing tag space</label
        >
      </div>
      <div class="p-field-checkbox">
        <Checkbox
          id="excludeComments"
          v-model="state.excludeComments"
          v-tooltip.right="'Remove comment tags'"
          :binary="true"
        />
        <label for="excludeComments">Exclude comments</label>
      </div>
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
import format from "xml-formatter";

import Button from "primevue/button";
import Card from "primevue/card";
import Checkbox from "primevue/checkbox";
import Dropdown from "primevue/dropdown";
import Tooltip from "primevue/tooltip";

import Editor from "@/components/Editor.vue";

type FormatOption = {
  text: string;
  value: string;
};

export default defineComponent({
  components: { Button, Card, Checkbox, Dropdown, Editor },
  directives: { Tooltip },
  setup() {
    const formatOptions = readonly([
      { text: "2 Spaces", value: " ".repeat(2) },
      { text: "4 Spaces", value: " ".repeat(4) },
      { text: "1 Tab", value: "\t" },
      { text: "Compact", value: "" }
    ] as FormatOption[]);
    const state = reactive({
      content: "<xml></xml>",
      formatOptionValue: formatOptions[0].value,
      collapseContent: false,
      whiteSpaceAtEndOfSelfclosingTag: false,
      excludeComments: false
    });
    const onClickFormat = () => {
      state.content = format(state.content, {
        indentation: state.formatOptionValue,
        collapseContent: state.collapseContent,
        whiteSpaceAtEndOfSelfclosingTag: state.whiteSpaceAtEndOfSelfclosingTag,
        filter: node => !state.excludeComments || node.type !== "Comment"
      });
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
