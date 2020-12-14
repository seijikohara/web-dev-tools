<template>
  <Card>
    <template #title> URL Encoding </template>
    <template #subtitle> URL Encoding & Decoding </template>
    <template #content>
      <Editor v-model:value="state.content" mode="text" height="500px" />
    </template>
    <template #footer>
      <div class="p-formgroup-inline">
        <Button label="Encode" @click="onClickEncode" />
        <Button label="Decode" @click="onClickDecode" />
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent, reactive } from "vue";

import Button from "primevue/button";
import Card from "primevue/card";

import Editor from "@/components/Editor.vue";

export default defineComponent({
  components: { Button, Card, Editor },
  async setup() {
    const state = reactive({
      content: "",
    });
    const onClickEncode = () => (state.content = encodeURI(state.content));
    const onClickDecode = () => (state.content = decodeURI(state.content));
    return {
      state,
      onClickEncode,
      onClickDecode,
    };
  },
});
</script>

<style lang="scss" scoped>
.p-button {
  margin-right: 0.5rem;
}
</style>
