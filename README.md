# BaseBot
Base robot code of FRC Team Galaxia #5987 in memory of David Zohar. Every new season, a new repository should be created with this repository as base.

_This repository will be un-archived after the 2019 season. Development during the season continues on [2019 robot code repository](https://github.com/Galaxia5987/robot-2019). All changes from `robot-2019` will be merged here after the 2019 season will end, to avoid constantly moving code from here to `robot-2019`._

## Features
- Basic Drivetrain code: motor controllers, encoders, driving with joystics. Note: amount and types of motor controllers and encoders may need to change.
- Pure Pursuit

## Expected changes on `robot-2019`
- Improved Pure Pursuit that stops in a precise location and heading
- Using [Dubins Path](https://en.wikipedia.org/wiki/Dubins_path) to generate paths
- Generating paths on-the-fly
- Updating setpoint in real-time
- Smooth transition from joystick driving to driving with Pure Pursuit.
