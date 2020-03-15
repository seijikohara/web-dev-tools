<template>
  <v-container fill-height fluid grid-list-xl>
    <v-row justify="center">
      <v-col cols="12">
        <material-card color="green" title="JSON" text="JSON Formatter">
          <editor
            v-model="content"
            lang="json"
            :requireModes="['json']"
            width="100%"
            height="500"
          />
          <div class="buttons">
            <v-btn @click="onClickFormat">Format</v-btn>
            <v-select
              label="Options"
              :items="formatOptions"
              v-model="formatOption"
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
    { text: "2 Spaces", value: "2spaces" },
    { text: "4 Spaces", value: "4spaces" },
    { text: "1 Tab", value: "1tab" },
    { text: "Compact", value: "compact" }
  ];
  formatOption = "2spaces";

  onEditorInput(val: string): void {
    this.content = val;
  }

  onClickFormat(): void {
    const parsed = JSON.parse(this.content);
    switch (this.formatOption) {
      case "2spaces":
        this.content = JSON.stringify(parsed, undefined, "  ");
        break;
      case "4spaces":
        this.content = JSON.stringify(parsed, undefined, "    ");
        break;
      case "1tab":
        this.content = JSON.stringify(parsed, undefined, "\t");
        break;
      case "compact":
        this.content = JSON.stringify(parsed);
        break;
    }
  }
}
</script>

<style lang="scss" scoped>
.buttons {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
}
</style>
