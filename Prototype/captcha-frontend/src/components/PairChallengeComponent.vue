<script>
import PairComponent from "@/components/PairComponent.vue";

export default {
  name: "PairChallengeComponent",
  computed: {
    PairComponent() {
      return PairComponent
    }
  },
  components: {
    PairComponent,

  }
};
</script>

<template>
<component :is="PairComponent"></component>
<component :is="PairComponent"></component>
<component :is="PairComponent"></component>
<component :is="PairComponent"></component>
</template>

<style scoped>

</style>