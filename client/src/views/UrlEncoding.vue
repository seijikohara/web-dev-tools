<template>
  <v-container fill-height fluid grid-list-xl>
    <v-row justify="center">
      <v-col cols="12">
        <material-card
          color="green"
          title="URL Encoding"
          text="URL Encoding & Decoding"
        >
          <editor
            v-model="content"
            lang="text"
            :requireModes="['text']"
            width="100%"
            height="300"
          ></editor>
          <div class="buttons">
            <v-btn @click="onClickEncode">Encode</v-btn>
            <v-btn @click="onClickDecode">Decode</v-btn>
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
export default class UrlEncoding extends Vue {
  content = "";

  onClickEncode(): void {
    this.content = encodeURI(this.content);
  }
  onClickDecode(): void {
    this.content = decodeURI(this.content);
  }
}
</script>

<style lang="scss" scoped>
.buttons {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
}
</style>
