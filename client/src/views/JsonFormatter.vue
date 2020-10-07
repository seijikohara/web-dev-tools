<template>
  <v-container fill-height fluid grid-list-xl>
    <v-row justify="center">
      <v-col cols="12">
        <material-card color="green" title="JSON" text="JSON Formatter">
          <editor
            v-model="content"
            mode="json"
            :requireModes="['json']"
            width="100%"
            height="500"
          />
          <div class="buttons">
            <v-btn @click="onClickFormat">Format</v-btn>
            <v-select
              label="Options"
              :items="formatOptions"
              v-model="formatOptionValue"
            />
          </div>
        </material-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Editor from "@/components/Editor.vue";

@Component({
  components: { Editor }
})
export default class JsonFormatter extends Vue {
  content = "{}";
  formatOptions: object[] = [
    { text: "2 Spaces", value: " ".repeat(2) },
    { text: "4 Spaces", value: " ".repeat(4) },
    { text: "1 Tab", value: "\t" },
    { text: "Compact", value: null }
  ];
  formatOptionValue = "  ";

  onEditorInput(val: string): void {
    this.content = val;
  }

  onClickFormat(): void {
    const parsed = JSON.parse(this.content);
    const padString = this.formatOptionValue;
    this.content = JSON.stringify(parsed, undefined, padString);
  }
}
</script>

<style lang="scss" scoped>
.buttons {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
}
</style>
