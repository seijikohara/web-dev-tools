<template>
  <v-container fill-height fluid grid-list-xl>
    <v-row justify="center">
      <v-col cols="12">
        <material-card color="green" title="Browser" text="Browser information">
          <v-row no-gutters>
            <v-col cols="12" sm="4">
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    Browser
                  </p>
                  <v-simple-table>
                    <template v-slot:default>
                      <thead>
                        <tr>
                          <th class="text-left">Name</th>
                          <th class="text-left">Version</th>
                          <th class="text-left">Engine</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>{{ browserInfo.browser.name }}</td>
                          <td>{{ browserInfo.browser.version }}</td>
                          <td>{{ browserInfo.engine.name }}</td>
                        </tr>
                      </tbody>
                    </template>
                  </v-simple-table>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" sm="4">
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    OS
                  </p>
                  <v-simple-table>
                    <template v-slot:default>
                      <thead>
                        <tr>
                          <th class="text-left">Name</th>
                          <th class="text-left">Version</th>
                          <th class="text-left">Version name</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>{{ browserInfo.os.name }}</td>
                          <td>{{ browserInfo.os.version }}</td>
                          <td>{{ browserInfo.os.versionName }}</td>
                        </tr>
                      </tbody>
                    </template>
                  </v-simple-table>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" sm="4">
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    Platform
                  </p>
                  <v-simple-table>
                    <template v-slot:default>
                      <thead>
                        <tr>
                          <th class="text-left">Type</th>
                          <th class="text-left">Vendor</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>{{ browserInfo.platform.type }}</td>
                          <td>{{ browserInfo.platform.vendor }}</td>
                        </tr>
                      </tbody>
                    </template>
                  </v-simple-table>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </material-card>
        <material-card color="green" title="Network" text="Network information">
          <div class="headline">{{ ipAddress }}</div>
        </material-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import * as Bowser from "bowser";

@Component({
  components: {}
})
export default class Dashboard extends Vue {
  ipAddress: string = "";
  browserInfo: object = {
    browser: { name: "", version: "" },
    os: { name: "", version: "", versionName: "" },
    platform: { type: "", vendor: "" },
    engine: { name: "" }
  };

  mounted(): void {
    this.browserInfo = Bowser.parse(window.navigator.userAgent);
    this.$http
      .get("/api/ip")
      .then(response => (this.ipAddress = response.data.ipAddress));
  }
}
</script>
