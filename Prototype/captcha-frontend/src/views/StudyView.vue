<template>
  <div class="StudyView">
    <StudyStartComponent v-if="showStudyStart" @studyStarted="startStudy"/>
    <PairChallengeComponent v-if="showPairChallenge" @submitPairChallenge="endPairChallenge"  :_pairs="pairs"/>
    <ListChallengeComponent v-if="showListChallenge" @submitListChallenge="endListChallenge"/>
  </div>
</template>

<script>
import StudyStartComponent from "@/components/study/StudyStartComponent.vue";
import PairChallengeComponent from "@/components/challenges/PairChallengeComponent.vue";
import ListChallengeComponent from "@/components/challenges/ListChallengeComponent.vue";
import StudyFinishedComponent from "@/components/study/StudyFinishedComponent.vue";

export default {
  name: "StudyView",
  components: {
    StudyStartComponent,
    StudyFinishedComponent,
    PairChallengeComponent,
    ListChallengeComponent
  },
  data() {
    return {
      studyStarted: false,
      studyReady: false,
      showStudyStart: true,
      showPairChallenge: false,
      showListChallenge: false,
      showStudyFinished: false,
      startTime: null,
      endTimePairChallenge: null,
      endTime: null,
      pairChallengeResults: null,
      listChallengeResults: null,
      studyResults: null,

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
    startStudy() {
      this.showStudyStart = false;
      this.showPairChallenge = true;
      this.startTime = Date.now(); // Store the start time
      console.log("Study started at:", this.startTime);
    },
    endPairChallenge() {
      this.showPairChallenge = false;
      this.showListChallenge = true;
      this.endTimePairChallenge = Date.now(); // Store the end time of the pair challenge
      console.log("Pair challenge ended at:", this.endTimePairChallenge);
      // Continue with your logic here...
    },
    endListChallenge() {
      this.showListChallenge = false;
      this.showStudyFinished = true;
      this.endTime = Date.now(); // Store the end time
      console.log("Study ended at:", this.endTime);
      // Continue with your logic here...
    }
  }
};
</script>

<style scoped>

</style>