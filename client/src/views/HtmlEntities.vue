<template>
  <Card>
    <template #title> HTML Entities </template>
    <template #subtitle> List of HTML entities </template>
    <template #content>
      <DataView
        :value="filteredEntities"
        :layout="state.layout"
        paginatorPosition="both"
        :paginator="true"
        :rows="200"
      >
        <template #header>
          <div class="p-grid p-nogutter">
            <div class="p-col-6" style="text-align: left">
              <div class="p-inputgroup">
                <span class="p-inputgroup-addon">
                  <i class="pi pi-search"></i>
                </span>
                <InputText placeholder="Search..." v-model="state.searchWord" />
              </div>
            </div>
            <div class="p-col-6" style="text-align: right">
              <DataViewLayoutOptions v-model="state.layout" />
            </div>
          </div>
        </template>
        <template #list="slotProps">
          <div class="p-col-12 p-md-1">
            <div class="char" v-html="slotProps.data.entityReference" />
          </div>
          <div class="p-col-12 p-md-11">
            <table class="list-table">
              <tr>
                <th>Code</th>
                <td>{{ slotProps.data.entityReference }}</td>
              </tr>
              <tr>
                <th>Name</th>
                <td>{{ slotProps.data.name }}</td>
              </tr>
              <tr>
                <th>Description</th>
                <td>{{ slotProps.data.description }}</td>
              </tr>
              <tr>
                <th>Standard</th>
                <td>{{ slotProps.data.standard }}</td>
              </tr>
              <tr>
                <th>DTD</th>
                <td>{{ slotProps.data.dtd }}</td>
              </tr>
            </table>
          </div>
        </template>
        <template #grid="slotProps">
          <div class="grid-container">
            <div
              class="char"
              v-html="slotProps.data.entityReference"
              :v-tooltip="slotProps.data.name"
            />
            <div class="code">
              {{ slotProps.data.entityReference }}
            </div>
          </div>
        </template>
      </DataView>
    </template>
  </Card>
</template>

<script lang="ts">
import { computed, defineComponent, reactive } from "vue";

import Card from "primevue/card";
import DataView from "primevue/dataview";
import DataViewLayoutOptions from "primevue/dataviewlayoutoptions";
import InputText from "primevue/inputtext";
import Tooltip from "primevue/tooltip";

import ApiService from "@/services/ApiService";

export default defineComponent({
  components: { Card, DataView, DataViewLayoutOptions, InputText },
  directives: { Tooltip },
  async setup() {
    const apiService = new ApiService();
    const htmlEntities = await apiService.getHtmlEntities();
    const state = reactive({
      layout: "list",
      searchWord: undefined,
    });
    const filteredEntities = computed(() =>
      htmlEntities.entities.filter((entity) => {
        const searchWord = state.searchWord;
        if (searchWord == null) return true;
        return (
          entity.name.includes(searchWord) ||
          entity.description?.includes(searchWord)
        );
      })
    );
    return {
      state,
      filteredEntities,
    };
  },
});
</script>

<style lang="scss" scoped>
.char {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 5rem;
  width: 5rem;
  font-size: 2.5rem;
  background-color: #eeeeee;
  margin: 0.5rem;
  transition: all 0.25s ease;
  &:hover {
    font-size: 3.5rem;
    box-shadow: 0 5px 10px 0 rgba(0, 0, 0, 0.125);
    transition: all 0.25s ease;
  }
}
.list-table {
  font-size: 0.75rem;
  th {
    text-align: right;
  }
}
.grid-container {
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  .code {
    font-size: 0.75rem;
  }
}
</style>
