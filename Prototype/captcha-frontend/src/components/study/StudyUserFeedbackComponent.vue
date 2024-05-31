<template>
  <div class="StudyUserFeedbackComponent">
    <h1>User Feedback</h1>
    <p>Thank you for participating in the study. Please provide feedback on your experience.</p>
    <h2>Pair Challenge:</h2>
    <p>How convenient and practical do you think is such type of Captcha? And would you prefer it over
      any different kind of Captcha (like Image-Captcha)? </p>
    <input type="text" v-model="pair_challenge_feedback" />
    <p>What is your opinion on the count of pairs in the pair challenge? How many would you be willing to solve without
      being
      majorly annoyed as a standard Captcha? </p>
    <input type="number" v-model="pair_challenge_count_opinion">
    <h2>List Challenge:</h2>
    <p>How convenient and practical do you think is such type of Captcha? And would you prefer it over the PairChallenge
      or any different kind of traditional Captcha (like Image-Captcha)? </p>
    <input type="text" v-model="list_challenge_feedback" />
    <p>What is your opinion on the count of usages in the list challenge? How many would you be willing to solve without
      being
      majorly annoyed as a standard Captcha? </p>
    <input type="number" v-model="list_challenge_count_opinion">
    <h2>General Feedback:</h2>
    <p>Any other feedback you would like to provide about the semantic NLP Captcha or to improve this study?</p>
    <input type="text" v-model="general_feedback">
    <button @click="submitFeedback">Submit Feedback</button>
  </div>
</template>

<script>
export default {
  name: "StudyUserFeedbackComponent",
  data() {
    return {
      pair_challenge_feedback: "",
      pair_challenge_count_opinion: null,
      list_challenge_feedback: "",
      list_challenge_count_opinion: null,
      general_feedback: ""
    };
  },
  methods: {
    submitFeedback() {

      // Check if all fields have been filled out
      if (this.pair_challenge_feedback === "" ||
        this.pair_challenge_count_opinion === null ||
        this.list_challenge_feedback === "" ||
        this.list_challenge_count_opinion === null
      ) {
        alert("Please fill out all fields before submitting.");
        return;
      }


      // Create an object with all the feedback fields
      const feedback = {
        pairChallengeFeedback: this.pair_challenge_feedback,
        pairChallengeCountOpinion: this.pair_challenge_count_opinion,
        listChallengeFeedback: this.list_challenge_feedback,
        listChallengeCountOpinion: this.list_challenge_count_opinion,
        generalFeedback: this.general_feedback
      };

      this.$emit("submitFeedback", feedback);
    }
  }
};
</script>

<style scoped>

</style>