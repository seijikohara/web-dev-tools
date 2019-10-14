<template>
  <v-footer id="core-footer" absolute height="40">
    <div class="footer-items"></div>
    <v-spacer />
    <span class="font-weight-light copyright">
      &copy;
      {{ new Date().getFullYear() }}
      Seiji Kohara
    </span>
  </v-footer>
</template>

<style scoped>
#core-footer {
  z-index: 0;
}
</style>
