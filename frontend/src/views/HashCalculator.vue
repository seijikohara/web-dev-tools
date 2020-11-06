<template>
  <Card>
    <template #title> Hash </template>
    <template #subtitle> Hash calculator </template>
    <template #content>
      <div class="p-fluid">
        <div class="p-field">
          <Editor v-model:value="state.text" mode="text" height="200px" />
        </div>
        <div class="p-field">
          <DataTable :value="hashedValues" class="p-datatable-sm">
            <template #header> Hash values </template>
            <Column
              field="method"
              header="Method"
              :headerStyle="{ width: '150px' }"
              :sortable="true"
            />
            <Column field="value" header="Value" />
          </DataTable>
        </div>
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { computed, defineComponent, reactive } from "vue";
import CryptoJS from "crypto-js";

import Card from "primevue/card";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

import Editor from "@/components/Editor.vue";

export default defineComponent({
  components: { Card, DataTable, Column, Editor },
  setup() {
    const state = reactive({
      text: "",
    });
    const hashedValues = computed(() => {
      const value = state.text;
      return [
        { method: "md5", value: CryptoJS.MD5(value).toString() },
        { method: "sha1", value: CryptoJS.SHA1(value).toString() },
        { method: "sha224", value: CryptoJS.SHA224(value).toString() },
        { method: "sha256", value: CryptoJS.SHA256(value).toString() },
        { method: "sha384", value: CryptoJS.SHA384(value).toString() },
        { method: "sha512", value: CryptoJS.SHA512(value).toString() },
      ];
    });
    return {
      state,
      hashedValues,
    };
  },
});
</script>

<style lang="scss" scoped>
.method-column {
  width: 150px;
}
</style>
