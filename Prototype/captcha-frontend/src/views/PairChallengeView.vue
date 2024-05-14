<script>
import CaptchaSuccessful from "@/components/CaptchaSuccessful.vue";
import CaptchaFailed from "@/components/CaptchaFailed.vue";
import PairChallengeComponent from "@/components/PairChallengeComponent.vue";

export default {
  name: "PairView",
  components: {
    PairChallengeComponent,
    CaptchaSuccessful,
    CaptchaFailed
  },
  data() {
    return {
      currentComponent: PairChallengeComponent,
      componentsArray: ['PairChallengeComponent', 'CaptchaSuccessful', 'CaptchaFailed']
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