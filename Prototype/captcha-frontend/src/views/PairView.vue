<script>
import PairComponent from "@/components/challenges/PairComponent.vue";
import CaptchaSuccessful from "@/components/misc/CaptchaSuccessful.vue";
import CaptchaFailed from "@/components/misc/CaptchaFailed.vue";

export default {
  name: "PairView",
  components: {
    PairComponent,
    CaptchaSuccessful,
    CaptchaFailed
  },
  data() {
    return {
      currentComponent: PairComponent,
      componentsArray: ['PairComponent', 'CaptchaSuccessful', 'CaptchaFailed']
    };
  },
  methods: {
    swapComponent(successful) {
      console.log("Current Component: " + this.currentComponent);
      console.log("Successful: " + successful);

      if (successful) {
        this.currentComponent = 'CaptchaSuccessful';
      } else {
        this.currentComponent = 'CaptchaFailed';
      }
    }
  }
};
</script>

<template>
  <component :is="currentComponent" @submit="swapComponent"></component>

</template>

<style scoped>

</style>