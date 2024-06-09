<template>
  <div class="StudyUserFeedbackComponent">
    <h1>User Feedback</h1>
    <p>Thank you for participating in the study. This study helps to develop a new type of "Are you human?" detection
      program called "semantic NLP CAPTCHA". <br> Please provide feedback on your experience.</p>
    <h2>General Information:</h2>
    <p>Is english your mother tongue ?</p>
    <select v-model="mother_language">
      <option value="yes">Yes</option>
      <option value="no">No</option>
      <option value="">No answer</option>
    </select>

    <h2>Challenge:</h2>

    <p>How easy was it to determine the similarity of the highlighted words in general? Rate on a scale from 1
      being impossible and 10 being trivial to determine: </p>
    <input type="number" v-model="rating_determine_semantic_meaning">

    <p>How convenient and practical do you think is such type of Challenge ? </p>
    <input type="text" v-model="pair_challenge_feedback1" />

    <p>And would you prefer it over any different kind of Captcha (like Image-Captcha)? </p>
    <input type="text" v-model="pair_challenge_feedback2" />

    <p>What is your opinion on the count of pairs in the pair challenge? How many would you be willing to solve without
      being majorly annoyed as a standard Captcha? </p>
    <input type="number" v-model="pair_challenge_count_opinion">
    <h2>List Challenge:</h2>
    <p>How convenient and practical do you think is such type of Captcha? </p>
    <input type="text" v-model="list_challenge_feedback1" />
    <p>And would you prefer it over the PairChallenge or any different kind of traditional Captcha (like
      Image-Captcha)? </p>
    <input type="text" v-model="list_challenge_feedback2" />
    <p>What is your opinion on the count of usages in the list challenge? How many would you be willing to solve without
      being majorly annoyed as a standard Captcha? </p>
    <input type="number" v-model="list_challenge_count_opinion">
    <h2>General Feedback:</h2>
    <p>Any other feedback you would like to provide about the semantic NLP Captcha or to improve this study?</p>
    <input type="text" v-model="general_feedback">
    <p>Thank you for your feedback!</p>
    <button @click="submitFeedback">Submit Feedback</button>
  </div>
</template>

<script>
export default {
  name: "StudyUserFeedbackComponent",
  data() {
    return {
      mother_language: "",
      rating_determine_semantic_meaning: null,
      pair_challenge_feedback1: "",
      pair_challenge_feedback2: "",
      pair_challenge_count_opinion: null,
      list_challenge_feedback1: "",
      list_challenge_feedback2: "",
      list_challenge_count_opinion: null,
      general_feedback: ""
    };
  },
  methods: {
    submitFeedback() {

      // Check if all fields have been filled out
      // if (this.pair_challenge_feedback === "" ||
      //   this.pair_challenge_count_opinion === null ||
      //   this.list_challenge_feedback === "" ||
      //   this.list_challenge_count_opinion === null
      // ) {
      //   alert("Please fill out all fields before submitting.");
      //   return;
      // }


      // Create an object with all the feedback fields
      const feedback = {
        motherLanguage: this.mother_language,
        ratingDetermineSemanticMeaning: this.rating_determine_semantic_meaning,
        pairChallengeFeedback: this.pair_challenge_feedback1 + " | " + this.pair_challenge_feedback2,
        pairChallengeCountOpinion: this.pair_challenge_count_opinion,
        listChallengeFeedback: this.list_challenge_feedback1 + " | " + this.list_challenge_feedback2,
        listChallengeCountOpinion: this.list_challenge_count_opinion,
        generalFeedback: this.general_feedback
      };

      this.$emit("submitFeedback", feedback);
    }
  }
};
</script>

<style scoped>
input[type="text"] {
  width: 80%;
}
</style>