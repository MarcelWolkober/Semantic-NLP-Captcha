<template>
  <div class="PairChallengeComponent">
<!--    <h1> Pair Challenge </h1>-->
    <h3> Solve all the following challenges:</h3>
    <PairComponent v-for="(pair, index) in _pairs"
                   :key="index" :pair="pair" :pairNumber="index + 1" @userChoice="addUserChoice" />
    <div class="button-submit">
      <p v-if="!userNotSelectedAllQuery"> Submit your choices</p>
      <p v-else style="color: red"> You have not solved all challenges</p>
      <button id="submit" @click=submitUserChoice>Submit</button>
    </div>
  </div>
</template>


<script>
import PairComponent from "@/components/challenges/PairComponent.vue";

export default {
  name: "PairChallengeComponent",
  components: {
    PairComponent
  },
  props: {
    _id: Number,
    _pairs: {
      type: Array,
      required: true
    }

  },
  data() {
    return {
      id: this._id,
      pairs: this._pairs,
      userChoices: [],
      userNotSelectedAllQuery: false
    };
  },
  watch: {
    _pairs: {
      immediate: true,
      handler(newVal, oldVal) {
        console.log("Pairs changed");
        console.log(newVal);
        this.pairs = newVal;
      }
    }
  },
  mounted() {
    console.log("PairChallengeComponent mounted");
    console.log("Pairs ", this._pairs);
  },
  methods: {
    addUserChoice(choice) {
      // Find the index of the existing choice for the pair
      const index = this.userChoices.findIndex(c => c.id === choice.id);

      if (index !== -1) {
        // If the choice exists, update it
        this.userChoices[index] = choice;
      } else {
        // If the choice doesn't exist, add it
        this.userChoices.push(choice);
      }
    },
    submitUserChoice() {
      if (this.userChoices.length !== this.pairs.length) {
        this.userNotSelectedAllQuery = true;
        console.log("User choices incomplete:", this.userChoices);

      } else {
        this.userNotSelectedAllQuery = false;
        console.log("User choices all:", this.userChoices);
        this.$emit("submitPairChallenge", {
          challengeId: this.id,
          userChoices: this.userChoices
        });
      }
    }
  }
};
</script>


<style scoped>
.PairComponent {
  margin-bottom: 50px; /* Adjust as needed */
}
</style>