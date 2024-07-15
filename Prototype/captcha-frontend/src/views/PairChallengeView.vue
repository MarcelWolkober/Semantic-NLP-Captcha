<template>
  <!--  <component :is="currentComponent" @submit="swapComponent"></component>-->
  <PairChallengeComponent :_pairs="pairs" />
</template>

<script>
import CaptchaSuccessful from "@/components/misc/CaptchaSuccessful.vue";
import CaptchaFailed from "@/components/misc/CaptchaFailed.vue";
import PairChallengeComponent from "@/components/challenges/PairChallengeComponent.vue";

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
      componentsArray: ["PairChallengeComponent", "CaptchaSuccessful", "CaptchaFailed"],

      pairs: [
        {
          "id": 1,
          "usage1": {
            "id": 1,
            "lemma": "attack",
            "context": ", which should be the signal of an attack having been made there.",
            "posStartIndex": 35,
            "posEndIndex": 41
          },
          "usage2": {
            "id": 2,
            "lemma": "attack",
            "context": "commenced the life of a pioneer, and that, in a night attack, his cabin had been burned, his wife killed, and his son carried away by the savages.",
            "posStartIndex": 54,
            "posEndIndex": 60
          },
          "pairChallenge": null,
          "label": 4.0
        },
        {
          "id": 2,
          "usage1": {
            "id": 1,
            "lemma": "attack",
            "context": ", which should be the signal of an attack having been made there.",
            "posStartIndex": 35,
            "posEndIndex": 41
          },
          "usage2": {
            "id": 2,
            "lemma": "attack",
            "context": "commenced the life of a pioneer, and that, in a night attack, his cabin had been burned, his wife killed, and his son carried away by the savages.",
            "posStartIndex": 54,
            "posEndIndex": 60
          },
          "pairChallenge": null,
          "label": 4.0
        }]
    };
  },
  methods: {
    swapComponent(successful) {
      console.log("Current Component: " + this.currentComponent);
      console.log("Successful: " + successful);

      if (successful) {
        this.currentComponent = "CaptchaSuccessful";
      } else {
        this.currentComponent = "CaptchaFailed";
      }
    }
  }
};
</script>



<style scoped>

</style>