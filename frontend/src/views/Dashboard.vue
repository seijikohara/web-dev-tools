<template>
  <Card class="bottom-pad">
    <template v-slot:title> Browser </template>
    <template v-slot:subtitle> Browser information </template>
    <template v-slot:content>
      <div class="p-grid">
        <div class="p-col-12">
          <Panel header="User agent">
            {{ userAgent }}
          </Panel>
        </div>
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="Browser">
            <DataTable :value="browserInformation.browser">
              <Column field="name" header="Name" />
              <Column field="version" header="Version" />
            </DataTable>
          </Panel>
        </div>
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="Engine">
            <DataTable :value="browserInformation.engine">
              <Column field="name" header="Name" />
              <Column field="version" header="Version" />
            </DataTable>
          </Panel>
        </div>
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="OS">
            <DataTable :value="browserInformation.os">
              <Column field="name" header="Name" />
              <Column field="version" header="Version" />
            </DataTable>
          </Panel>
        </div>
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="Device">
            <DataTable :value="browserInformation.device">
              <Column field="model" header="Model" />
              <Column field="type" header="Type" />
              <Column field="vendor" header="Vendor" />
            </DataTable>
          </Panel>
        </div>
        <div class="p-col-12">
          <Panel header="HTTP headers">
            <DataTable
              :value="httpHeaders.headers"
              :paginator="true"
              :rows="10"
              :rowsPerPageOptions="[10, 20, 50]"
              class="p-datatable-sm"
            >
              <Column
                field="name"
                header="Name"
                :headerStyle="{ width: '200px' }"
                :sortable="true"
              />
              <Column field="value" header="Value" :sortable="true" />
            </DataTable>
          </Panel>
        </div>
      </div>
    </template>
  </Card>
  <Card class="bottom-pad">
    <template v-slot:title> Network </template>
    <template v-slot:subtitle> Client network information </template>
    <template v-slot:content>
      <div class="p-grid">
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="IP address">
            {{ ipInfo.ipAddress }}
          </Panel>
        </div>
        <div class="p-col-12 p-md-6 p-lg-6">
          <Panel header="Host name">
            {{ ipInfo.hostName }}
          </Panel>
        </div>
        <div class="p-col-12 p-md-12 p-lg-6">
          <Panel header="Geo location">
            <JsonTreeView :data="JSON.stringify(geo)" :maxDepth="100" />
          </Panel>
        </div>
        <div class="p-col-12 p-md-12 p-lg-6">
          <Panel header="RDAP infromation">
            <JsonTreeView :data="JSON.stringify(rdap)" :maxDepth="100" />
          </Panel>
        </div>
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import UAParser from "ua-parser-js";
import { JsonTreeView } from "json-tree-view-vue3";

import Card from "primevue/card";
import Panel from "primevue/panel";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

import ApiService from "@/services/ApiService";

const userAgent = window.navigator.userAgent;
const uaParser = new UAParser();
const bowserResult = uaParser.getResult();

export default defineComponent({
  components: { Card, Panel, DataTable, Column, JsonTreeView },
  async setup() {
    const apiService = new ApiService();
    const ipInfo = await apiService.getIpAddress();
    const httpHeaders = await apiService.getHttpHeader();
    const ipAddress = ipInfo.ipAddress;
    const geo = await apiService.getGeo(ipAddress);
    const rdap = await apiService.getRdap(ipAddress);
    return {
      userAgent,
      browserInformation: {
        browser: [bowserResult.browser],
        engine: [bowserResult.engine],
        os: [bowserResult.os],
        device: [bowserResult.device],
      },
      ipInfo,
      httpHeaders,
      geo,
      rdap,
    };
  },
});
</script>

<style lang="scss" scoped>
.bottom-pad {
  margin-bottom: 1rem;
}
</style>
