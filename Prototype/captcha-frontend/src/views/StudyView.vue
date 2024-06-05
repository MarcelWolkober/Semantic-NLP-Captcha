<template>
  <div class="StudyView">
    <StudyStartComponent v-if="showStudyStart" @studyStarted="startStudy" />
    <PairChallengeComponent v-if="showPairChallenge" @submitPairChallenge="endPairChallenge" :_id="pair_challenge.id"
                            :_pairs="pairs" />
    <ListChallengeComponent v-if="showListChallenge" @submitListChallenge="endListChallenge" :_id="list_challenge.id"
                            :_lemma="list_challenge.lemma" :_reference-usage="list_challenge.referenceUsage"
                            :_usages="list_challenge.listUsages" />
    <StudyUserFeedbackComponent v-if="showUserFeedback" @submitFeedback="submitFeedback" />
    <StudyFinishedComponent v-if="showStudyFinished" :userFeedback="userFeedBack" />
    <StudyErrorPageComponent v-if="showStudyErrorPage" />
  </div>
</template>

<script>
import StudyStartComponent from "@/components/study/StudyStartComponent.vue";
import PairChallengeComponent from "@/components/challenges/PairChallengeComponent.vue";
import ListChallengeComponent from "@/components/challenges/ListChallengeComponent.vue";
import StudyFinishedComponent from "@/components/study/StudyFinishedComponent.vue";
import StudyDataService from "@/services/StudyDataService.js";
import StudyUserFeedbackComponent from "@/components/study/StudyUserFeedbackComponent.vue";
import StudyErrorPageComponent from "@/components/study/StudyErrorPageComponent.vue";
import axios from "axios";

export default {
  name: "StudyView",
  components: {
    StudyStartComponent,
    StudyUserFeedbackComponent,
    StudyFinishedComponent,
    PairChallengeComponent,
    ListChallengeComponent,
    StudyErrorPageComponent
  },
  data() {
    return {
      studyStarted: false,
      studyReady: false,

      showStudyStart: true,
      showPairChallenge: false,
      showListChallenge: false,
      showUserFeedback: false,
      showStudyFinished: false,
      showStudyErrorPage: false,

      study_challenge: null,
      pair_challenge: null,
      pairs: null,
      list_challenge: null,


      startTime: null,
      endTimePairChallenge: null,
      endTime: null,

      pairChallengeResults: null,
      listChallengeResults: null,
      userFeedBack: null


    };
  },
  methods: {
    startStudy() {
      StudyDataService.getNewStudy().then(response => {
        if (response.status !== 200) {
          console.log("No correct answer received from server", response.status, response.data);
          this.showStudyStart = false;
          this.showStudyErrorPage = true;
        } else {
          console.log("Study challenge received", response.status, response.data);

          this.study_challenge = response.data;

          this.pair_challenge = response.data.pairChallenge;
          this.pairs = response.data.pairChallenge.usagePairs;
          this.list_challenge = response.data.listRankingChallenge;

          console.log("Study challenge:", this.study_challenge);
          console.log("Pair challenge:", this.pair_challenge);
          console.log("List challenge:", this.list_challenge);

          this.showStudyStart = false;
          this.showPairChallenge = true;
          this.startTime = Date.now(); // Store the start time
          console.log("Study started at:", this.startTime);
        }
      }).catch(e => {
        console.log("Error getting study challenge", e);
        this.showStudyStart = false;
        this.showStudyErrorPage = true;
      });


    },
    endPairChallenge(challengeId, userChoices) {
      this.showPairChallenge = false;
      this.showListChallenge = true;
      this.endTimePairChallenge = Date.now(); // Store the end time of the pair challenge
      console.log("Pair challenge ended at:", this.endTimePairChallenge);

      this.pairChallengeResults = {
        pairChallengeId: challengeId,
        chosenLabel: userChoices
      };


    },
    endListChallenge(challengeId, order) {
      this.showListChallenge = false;
      this.showUserFeedback = true;
      this.endTime = Date.now(); // Store the end time
      console.log("Study ended at:", this.endTime);


      this.listChallengeResults = {
        listChallengeId: challengeId,
        order: order
      };


    },
    submitFeedback(feedback) {


      this.userFeedBack = feedback;

      console.log("Feedback submitted", feedback);

      const studyResults = {
        studyChallengeId: this.study_challenge.id,
        startTime: this.startTime,
        endTimePairChallenge: this.endTimePairChallenge,
        endTime: this.endTime,
        pairChallengeResults: this.pairChallengeResults,
        listChallengeResults: this.listChallengeResults,
        userFeedback: feedback
      };

      console.log("Study results:", studyResults);

      StudyDataService.postStudy(studyResults).then(response => {

        if (response.status === 200) {
          console.log("Study results posted successfully");
          console.log("response: ", response.data);
          this.showUserFeedback = false;
          this.showStudyFinished = true;

        } else {
          console.log("Study results not posted successfully");
          this.showUserFeedback = false;
          this.showStudyErrorPage = true;
        }

      }).catch(e => {
        console.log("Error posting study results", e);
        this.showUserFeedback = false;
        this.showStudyErrorPage = true;
      });


    }
  },
  created() {
    console.log("StudyView created");
    axios.defaults.baseURL = 'http://193.196.55.76/api';  }
};
</script>

<style scoped>

</style>