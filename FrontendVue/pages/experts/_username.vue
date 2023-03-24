<template>
  <div>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
      <div class="container">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNavDropdown">
          <ul class="navbar-nav mx-auto">
            <li class="nav-item">
              <nuxt-link :to="`/experts/${this.username}`" class="nav-link mx-2 active"
                aria-current="page">Profile</nuxt-link>
            </li>
            <li class="nav-item">
              <nuxt-link to="/occurrences" class="nav-link mx-2">Occurrences</nuxt-link>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="mx-auto" style="width: 600px; margin-top: 50px;">
      <b-container class="h-100" :expertInsurance="expert.insurance">
        <b-row class="h-100">
          <b-col md="8" class="d-flex flex-column align-items-center">
            <h4>Expert Details:</h4>
            <p>Username: {{ expert.username }}</p>
            <p>Name: {{ expert.name }}</p>
            <p>Email: {{ expert.email }}</p>
            <p>Insurance: {{ expert.insurance }}</p>
          </b-col>
        </b-row>
      </b-container>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      expert: {}
    }
  },
  computed: {
    username() {
      return this.$route.params.username
    }
  },
  created() {
    this.$axios.$get(`api/experts/${this.username}`)
      .then(expert => this.expert = expert || {})
  },
  methods: {
    signOut() {
      this.$auth.logout()
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>

</style>
