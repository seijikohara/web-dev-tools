<template>
  <v-container fill-height fluid grid-list-xl>
    <v-row justify="center">
      <v-col cols="12">
        <material-card color="green" title="Browser" text="Browser information">
          <p class="display-1 text--primary">
            User agent
          </p>
          <p>{{ userAgent }}</p>
          <v-row>
            <v-col>
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
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>{{ browserInfo.browser.name }}</td>
                          <td>{{ browserInfo.browser.version }}</td>
                        </tr>
                      </tbody>
                    </template>
                  </v-simple-table>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col>
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    Engine
                  </p>
                  <v-simple-table>
                    <template v-slot:default>
                      <thead>
                        <tr>
                          <th class="text-left">Name</th>
                          <th class="text-left">Version</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>{{ browserInfo.engine.name }}</td>
                          <td>{{ browserInfo.engine.version }}</td>
                        </tr>
                      </tbody>
                    </template>
                  </v-simple-table>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
          <v-row>
            <v-col>
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
            <v-col>
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
          <v-row>
            <v-col>
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    HTTP headers
                  </p>
                  <v-data-table
                    :headers="httpTableHeaders"
                    :items="httpTableData"
                    :items-per-page="5"
                  />
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </material-card>
        <material-card color="green" title="Network" text="Network information">
          <v-row>
            <v-col>
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    IP address
                  </p>
                  <p>{{ ipAddress }}</p>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col>
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    Host name
                  </p>
                  <p>{{ hostName }}</p>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    Geo location
                  </p>
                  <json-view :data="geo" :maxDepth="100" rootKey="/" />
                </v-card-text>
              </v-card>
            </v-col>
            <v-col>
              <v-card>
                <v-card-text>
                  <p class="display-1 text--primary">
                    RDAP infromation
                  </p>
                  <json-view :data="rdap" :maxDepth="100" rootKey="/" />
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </material-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import axios from "axios";
import Bowser, { Parser } from "bowser";
@Component({
  components: {}
})
export default class Dashboard extends Vue {
  userAgent = "";
  browserInfo: Parser.ParsedResult = {
    browser: { name: "", version: "" },
    os: { name: "", version: "", versionName: "" },
    platform: { type: "", vendor: "" },
    engine: { name: "", version: "" }
  };
  httpTableHeaders = [
    { text: "Name", value: "name" },
    { text: "Value", value: "value" }
  ];
  httpTableData = [];
  ipAddress = "";
  hostName = "";
  rdap = {};
  geo = {};

  async mounted() {
    const userAgent = window.navigator.userAgent;
    this.userAgent = userAgent;
    this.browserInfo = Bowser.parse(userAgent);

    axios
      .get("/api/http-headers")
      .then(response => (this.httpTableData = response.data.headers));

    const ipResponse = await axios.get("/api/ip");
    this.ipAddress = ipResponse.data.ipAddress;
    this.hostName = ipResponse.data.hostName;

    axios.get(`/api/rdap/${this.ipAddress}`).then(response => {
      this.rdap = JSON.parse(response.data.rdap);
    });
    axios.get(`/api/geo/${this.ipAddress}`).then(response => {
      this.geo = JSON.parse(response.data.geo);
    });
  }
}
</script>
