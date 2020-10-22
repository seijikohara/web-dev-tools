<template>
  <div class="json-view-item">
    <!-- Handle Objects and Arrays-->
    <div v-if="data.type === 'object' || data.type === 'array'">
      <button
        @click.stop="toggleOpen"
        class="data-key"
        :aria-expanded="state.open ? 'true' : 'false'"
      >
        <div :class="classes"></div>
        {{ data.key }}:
        <span class="properties">{{ lengthString }}</span>
      </button>
      <JsonViewItem
        v-on:selected="bubbleSelected"
        v-for="child in data.children"
        :key="getKey(child)"
        :data="child"
        v-show="state.open"
        :maxDepth="maxDepth"
        :canSelect="canSelect"
      />
    </div>
    <!-- Handle Leaf Values -->
    <div
      :class="valueClasses"
      v-on:click="onClick(data)"
      @keyup.enter="onClick(data)"
      @keyup.space="onClick(data)"
      :role="canSelect ? 'button' : undefined"
      :tabindex="canSelect ? '0' : undefined"
      v-if="data.type === 'value'"
    >
      <span class="value-key">{{ data.key }}:</span>
      <span :style="getValueStyle(data.value)">
        {{ dataValue }}
      </span>
    </div>
  </div>
</template>

<script lang="ts">
import {
  computed,
  defineComponent,
  PropType,
  reactive,
  SetupContext
} from "vue";

export interface SelectedData {
  key: string;
  value: string;
  path: string;
}
export interface Data {
  [key: string]: string;
}

export type ItemData = {
  key: string;
  type: string;
  path: string;
  depth: number;
  length?: number;
  children?: ItemData[];
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  value?: any;
};

type Props = {
  data: ItemData;
  maxDepth: number;
  canSelect: boolean;
};

export default defineComponent({
  name: "JsonViewItem",
  props: {
    data: {
      required: true,
      type: Object as PropType<ItemData>
    },
    maxDepth: {
      type: Number,
      required: false,
      default: 1
    },
    canSelect: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  setup(props: Props, context: SetupContext) {
    const state = reactive({
      open: props.data.depth < props.maxDepth
    });
    const toggleOpen = () => {
      state.open = !state.open;
    };
    const onClick = (data: Data) => {
      context.emit("selected", {
        key: data.key,
        value: data.value,
        path: data.path
      } as SelectedData);
    };
    const bubbleSelected = (data: Data) => context.emit("selected", data);
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const getKey = (value: any): string => {
      if (!isNaN(value.key)) {
        return `${value.key}":`;
      } else {
        return `"${value.key}":`;
      }
    };
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const getValueStyle = (value: any): object => {
      const type = typeof value;
      switch (type) {
        case "string":
          return { color: "var(--jtv-string-color)" };
        case "number":
          return { color: "var(--jtv-number-color)" };
        case "boolean":
          return { color: "var(--jtv-boolean-color)" };
        case "object":
          return { color: "var(--jtv-null-color)" };
        case "undefined":
          return { color: "var(--jtv-null-color)" };
        default:
          return { color: "var(--jtv-valueKey-color)" };
      }
    };
    const classes = computed((): object => {
      return {
        "chevron-arrow": true,
        opened: state.open
      };
    });
    const valueClasses = computed((): object => {
      return {
        "value-key": true,
        "can-select": props.canSelect
      };
    });
    const lengthString = computed((): string => {
      if (props.data.type === "array") {
        return props.data.length === 1
          ? props.data.length + " element"
          : props.data.length + " elements";
      }
      return props.data.length === 1
        ? props.data.length + " property"
        : props.data.length + " properties";
    });
    const dataValue = computed((): string => {
      if (typeof props.data.value === "undefined") {
        return "undefined";
      }
      return JSON.stringify(props.data.value);
    });
    return {
      state,
      toggleOpen,
      onClick,
      bubbleSelected,
      getKey,
      getValueStyle,
      classes,
      valueClasses,
      lengthString,
      dataValue
    };
  }
});
</script>

<style lang="scss">
.json-view-item:not(.root-item) {
  margin-left: 15px;
}
.value-key {
  color: var(--jtv-valueKey-color);
  font-weight: 600;
  margin-left: 10px;
  border-radius: 2px;
  white-space: nowrap;
  padding: 5px 5px 5px 10px;
  &.can-select {
    cursor: pointer;
    &:hover {
      background-color: rgba(0, 0, 0, 0.08);
    }
    &:focus {
      outline: 2px solid var(--jtv-hover-color);
    }
  }
}
.data-key {
  // Button overrides
  font-size: 100%;
  font-family: inherit;
  border: 0;
  background-color: transparent;
  width: 100%;
  // Normal styles
  color: var(--jtv-key-color);
  display: flex;
  align-items: center;
  border-radius: 2px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  padding: 5px;
  &:hover {
    background-color: var(--jtv-hover-color);
  }
  &:focus {
    outline: 2px solid var(--jtv-hover-color);
  }
  &::-moz-focus-inner {
    border: 0;
  }
  .properties {
    font-weight: 300;
    opacity: 0.9;
    margin-left: 4px;
    user-select: none;
  }
}
.chevron-arrow {
  flex-shrink: 0;
  border-right: 2px solid var(--jtv-arrow-color);
  border-bottom: 2px solid var(--jtv-arrow-color);
  width: var(--jtv-arrow-size);
  height: var(--jtv-arrow-size);
  margin-right: 20px;
  margin-left: 5px;
  transform: rotate(-45deg);
  &.opened {
    margin-top: -3px;
    transform: rotate(45deg);
  }
}
</style>
