<template>
  <ace
    v-model="content"
    @init="aceInit"
    :lang="lang"
    :theme="theme"
    :width="width"
    :height="height"
  ></ace>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit, Watch } from "vue-property-decorator";

@Component({
  components: { ace: require("vue2-ace-editor") }
})
export default class Editor extends Vue {
  @Prop({ default: "html" })
  lang!: string;
  @Prop({ default: "chrome" })
  theme!: string;
  @Prop({ default: "100%" })
  width!: string;
  @Prop({ default: "100%" })
  height!: string;
  @Prop({ default: "" })
  value!: string;
  @Prop({ default: () => ["html"] })
  requireModes!: string[];
  @Prop({ default: () => ["chrome"] })
  requireThemes!: string[];

  content = "";

  @Emit("input")
  input(val: string): string {
    return val;
  }

  @Watch("content")
  onEditorContentChanged(newVal: string) {
    this.input(newVal);
  }

  @Watch("value")
  onValueChanged(newVal: string) {
    this.content = newVal;
  }

  mounted(): void {
    this.content = this.value;
  }

  aceInit(): void {
    require("brace/ext/language_tools"); //language extension prerequsite...
    this.requireModes.forEach(mode => require(`brace/mode/${mode}`));
    this.requireThemes.forEach(theme => require(`brace/theme/${theme}`));
  }
}
</script>
