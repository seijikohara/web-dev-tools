<template>
  <VAceEditor
    v-model:value="state.content"
    :lang="mode"
    :theme="theme"
    :style="styles"
    :options="{
      ...{ showPrintMargin: false, showInvisibles: true },
      ...options
    }"
  />
</template>

<script lang="ts">
import { computed, defineComponent, reactive, SetupContext, watch } from "vue";

import { VAceEditor } from "vue3-ace-editor";

type Props = {
  mode: string;
  theme: string;
  width: string;
  height: string;
  value: string;
  options?: object;
};

export default defineComponent({
  name: "Editor",
  components: { VAceEditor },
  props: {
    mode: {
      type: String,
      default: "text",
      required: true
    },
    theme: {
      type: String,
      default: "chrome",
      required: true
    },
    width: {
      type: String,
      default: "100%",
      required: true
    },
    height: {
      type: String,
      default: "100%",
      required: true
    },
    value: {
      type: String,
      default: ""
    },
    options: {
      type: Object,
      required: false
    }
  },
  setup(props: Props, context: SetupContext) {
    const state = reactive({
      content: props.value
    });
    const styles = computed(() => {
      return {
        height: props.height,
        width: props.width
      };
    });
    const valueChanged = watch(
      () => props.value,
      value => (state.content = value)
    );
    const contentChanged = watch(
      () => state.content,
      value => context.emit("update:value", value)
    );
    return {
      state,
      styles,
      valueChanged,
      contentChanged
    };
  }
});
</script>
