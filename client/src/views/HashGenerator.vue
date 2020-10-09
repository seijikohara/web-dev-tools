<template>
  <v-container fill-height fluid grid-list-xl>
    <v-row justify="center">
      <v-col cols="12">
        <material-card color="green" title="Hash" text="Hash generator">
          <editor
            v-model="text"
            mode="text"
            :requireModes="['text']"
            width="100%"
            height="200"
          />
          <v-text-field v-model="hash.md5" label="MD5" readonly />
          <v-text-field v-model="hash.sha1" label="SHA1" readonly />
          <v-text-field v-model="hash.sha224" label="SHA224" readonly />
          <v-text-field v-model="hash.sha256" label="SHA256" readonly />
          <v-text-field v-model="hash.sha384" label="SHA384" readonly />
          <v-text-field v-model="hash.sha512" label="SHA512" readonly />
        </material-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import Vue from "vue";
import CryptoJS from "crypto-js";

import Editor from "@/components/Editor.vue";

export default Vue.extend({
  name: "HashGenerator",
  components: { Editor },
  data() {
    return {
      text: "",
      hash: {
        md5: "",
        sha1: "",
        sha224: "",
        sha256: "",
        sha384: "",
        sha512: ""
      }
    };
  },
  watch: {
    text(value: string): void {
      this.hash.md5 = CryptoJS.MD5(value).toString();
      this.hash.sha1 = CryptoJS.SHA1(value).toString();
      this.hash.sha224 = CryptoJS.SHA224(value).toString();
      this.hash.sha256 = CryptoJS.SHA256(value).toString();
      this.hash.sha384 = CryptoJS.SHA384(value).toString();
      this.hash.sha512 = CryptoJS.SHA512(value).toString();
    }
  }
});
</script>
