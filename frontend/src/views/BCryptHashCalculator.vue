<template>
  <Card>
    <template #title> BCyprt </template>
    <template #subtitle> BCrypt hash calculator </template>
    <template #content>
      <div class="p-fluid">
        <div class="p-field">
          <label for="password">Password</label>
          <InputText id="password" type="text" v-model="state.password" />
        </div>
        <div class="p-field">
          <label for="rounds">Rounds</label>
          <InputNumber id="rounds" v-model="state.rounds" :min="4" :max="20" />
          <Slider v-model="state.rounds" :min="4" :max="20" />
        </div>
        <div class="p-field">
          <Fieldset legend="Hashed password">
            <code>{{ hashedValue }}</code>
          </Fieldset>
        </div>
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent, reactive, computed } from "vue";
import * as bcrypt from "bcryptjs";

import Card from "primevue/card";
import Fieldset from "primevue/fieldset";
import InputNumber from "primevue/inputnumber";
import InputText from "primevue/inputtext";
import Slider from "primevue/slider";

export default defineComponent({
  components: { Card, Fieldset, InputNumber, InputText, Slider },
  async setup() {
    const state = reactive({
      password: "",
      rounds: 8,
    });
    const hashedValue = computed(() =>
      bcrypt.hashSync(state.password, state.rounds)
    );
    return {
      state,
      hashedValue,
    };
  },
});
</script>
