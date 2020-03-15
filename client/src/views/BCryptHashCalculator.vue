<template>
  <v-container fill-height fluid grid-list-xl>
    <v-row justify="center">
      <v-col cols="12">
        <material-card
          color="green"
          title="BCyprt"
          text="BCrypt hash calculator"
        >
          <v-text-field v-model="password" label="Password" required />
          <v-slider
            v-model="rounds"
            class="align-center"
            thumb-label
            label="Rounds"
            :max="20"
            :min="0"
            hide-details
          >
            <template v-slot:append>
              <v-text-field
                v-model="rounds"
                class="mt-0 pt-0"
                hide-details
                single-line
                type="number"
                style="width: 60px"
              ></v-text-field>
            </template>
          </v-slider>
          <v-text-field
            v-model="hashedValue"
            label="Hashed password"
            readonly
          />
        </material-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import * as bcrypt from "bcryptjs";

@Component({
  components: {}
})
export default class BCryptHashCalculator extends Vue {
  password = "";
  rounds = 8;

  public get hashedValue(): string {
    return bcrypt.hashSync(this.password, this.rounds);
  }
}
</script>
