<template>
  <JsonTreeViewItem
    :class="[{ 'root-item': true, dark: colorScheme === 'dark' }]"
    :data="parsed"
    :maxDepth="maxDepth"
    v-on:selected="itemSelected"
  />
</template>

<script lang="ts">
import { computed, defineComponent, SetupContext } from "vue";

import JsonTreeViewItem from "./JsonTreeViewItem.vue";

type Props = {
  data: object;
  rootKey: string;
  maxDepth: number;
  colorScheme: string;
};

export default defineComponent({
  name: "JsonTreeView",
  components: { JsonTreeViewItem },
  props: {
    data: {
      type: Object,
      required: true
    },
    rootKey: {
      type: String,
      required: false,
      default: "/"
    },
    maxDepth: {
      type: Number,
      required: false,
      default: 1
    },
    colorScheme: {
      type: String,
      required: false,
      default: "light"
    }
  },
  setup(props: Props, context: SetupContext) {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const isArray = (value: any): boolean => Array.isArray(value);
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const isObject = (value: any): boolean =>
      typeof value === "object" && value !== null && !isArray(value);
    const itemSelected = (data: object): void => context.emit("selected", data);
    const build = (
      key: string,
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      value: any,
      depth: number,
      path: string,
      includeKey: boolean
    ): object => {
      if (isObject(value)) {
        // Build Object
        const children = [];
        for (const [childKey, childValue] of Object.entries(value)) {
          children.push(
            build(
              childKey,
              childValue,
              depth + 1,
              includeKey ? `${path}${key}.` : `${path}`,
              true
            )
          );
        }
        return {
          key: key,
          type: "object",
          depth: depth,
          path: path,
          length: children.length,
          children: children
        };
      } else if (isArray(value)) {
        // Build Array
        const children = [];
        for (let i = 0; i < value.length; i++) {
          children.push(
            build(
              i.toString(),
              value[i],
              depth + 1,
              includeKey ? `${path}${key}[${i}].` : `${path}`,
              false
            )
          );
        }
        return {
          key: key,
          type: "array",
          depth: depth,
          path: path,
          length: children.length,
          children: children
        };
      } else {
        // Build Value
        return {
          key: key,
          type: "value",
          path: includeKey ? path + key : path.slice(0, -1),
          depth: depth,
          value: value
        };
      }
    };
    const parsed = computed((): object => {
      if (typeof props.data === "object") {
        return build(props.rootKey, { ...props.data }, 0, "", true);
      }
      return {
        key: props.rootKey,
        type: "value",
        path: "",
        depth: 0,
        value: props.data
      };
    });
    return {
      itemSelected,
      parsed
    };
  }
});
</script>

<style lang="scss" scoped>
.root-item {
  --jtv-key-color: #0977e6;
  --jtv-valueKey-color: #073642;
  --jtv-string-color: #268bd2;
  --jtv-number-color: #2aa198;
  --jtv-boolean-color: #cb4b16;
  --jtv-null-color: #6c71c4;
  --jtv-arrow-size: 6px;
  --jtv-arrow-color: #444;
  --jtv-hover-color: rgba(0, 0, 0, 0.1);
  margin-left: 0;
  width: 100%;
  height: auto;
}
.root-item.dark {
  --jtv-key-color: #80d8ff;
  --jtv-valueKey-color: #fdf6e3;
  --jtv-hover-color: rgba(255, 255, 255, 0.1);
  --jtv-arrow-color: #fdf6e3;
}
</style>
