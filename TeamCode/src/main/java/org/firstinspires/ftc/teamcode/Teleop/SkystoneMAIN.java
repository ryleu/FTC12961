/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.FPS.Drivetrain;
import org.firstinspires.ftc.teamcode.FPS.Odometry;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="DUALCONTROLLER", group="MAIN")
public class SkystoneMAIN extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private Drivetrain robot = new Drivetrain(hardwareMap);

    // Declare OpMode members.
    double gearSpeed = .7, fourbarPos = .9;
    double lB, lF, rB, rF;
    int goal;
    boolean winchToggle, capToggle, capDeployed = false, foundationToggle, toggle = false;

    //Odometry encoders = new Odometry();

    public void processUpdate() {
        robot.calculate(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_stick_y);
        //encoders.update(mecanum.finaltheta);
        // GEAR SPEED CALCULATIONS :
        if (!(gamepad1.dpad_down | gamepad1.dpad_up) && toggle) {
            toggle = false;
        }
        if (gamepad1.dpad_up && !toggle) {
            gearSpeed += .1;
            toggle = true;
        }
        if (gamepad1.dpad_down && !toggle) {
            gearSpeed -= .1;
            toggle = true;
        }
        gearSpeed = Range.clip(gearSpeed, .2, .9);

        lF = gearSpeed * robot.leftfront;
        lB = gearSpeed * robot.leftback;
        rF = gearSpeed * robot.rightfront;
        rB = gearSpeed * robot.rightback;
    }


    @Override
    public void runOpMode() {
        robot.map();
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Send calculated power to wheels
            if  (gamepad2.right_trigger != 0){
                robot.intakeLeft.setPower(.59);
                robot.intakeRight.setPower(-.59);
            } else if (gamepad2.left_trigger != 0){
                robot.intakeLeft.setPower(-.18);
                robot.intakeRight.setPower(.18);
            } else {
                robot.intakeLeft.setPower(0);
                robot.intakeRight.setPower(0);
            }


            // Lift Code
            if (gamepad2.a) {
                robot.winchRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.winchLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                robot.winchLeft.setPower(.7);
                robot.winchRight.setPower(.7);
                goal = (int)(robot.winchRight.getCurrentPosition()+robot.winchLeft.getCurrentPosition())/2;


            } else if (gamepad2.x) {
                robot.winchRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.winchLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.winchLeft.setPower(-.2);
                robot.winchRight.setPower(-.2);
                goal = (int)(robot.winchRight.getCurrentPosition()+robot.winchLeft.getCurrentPosition())/2;


            } else {
                robot.winchRight.setTargetPosition(goal);
                robot.winchLeft.setTargetPosition(goal);
                robot.winchRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.winchLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.winchLeft.setPower(0);
                robot.winchRight.setPower(0);
            }


            //virtual fourbar code
            if(gamepad2.y){
                fourbarPos = .9;
            } else if (gamepad2.b){
                fourbarPos = .1;
            }
            robot.fourbarRight.setPosition(fourbarPos);
            robot.fourbarLeft.setPosition(1-fourbarPos);
            if(gamepad2.right_bumper){
                robot.blockGrab.setPosition(0);
            } else {
                robot.blockGrab.setPosition(1);
            }


//            if (gamepad1.y){
//                capToggle = !capToggle;
//            } else {
//                if (capToggle){
//                    capToggle = false;
//                    capDeployed = !capDeployed;
//                }
//            }
//            if (capDeployed){
//                capstone.setPosition(1);
//            } else {
//                capstone.setPosition(0);
//            }
//            if (gamepad1.a){
//                capstone.setPosition(.35);
//            } else if (gamepad1.y){
//                capstone.setPosition(0);
//            }

            //Foundation Grabbers
            if (foundationToggle && gamepad1.right_bumper) {
                robot.leftHook.setPosition(.3);//deployed
                robot.rightHook.setPosition(.7);
                foundationToggle = !foundationToggle;
            } else if (!foundationToggle && gamepad1.right_bumper) {
                robot.leftHook.setPosition(.9); //retracted
                robot.rightHook.setPosition(.1);
                foundationToggle = !foundationToggle;
            }

            robot.leftFront.setPower(Range.clip(lF, -1, 1));
            robot.leftBack.setPower(Range.clip(lB, -1, 1));
            robot.rightFront.setPower(Range.clip(rF, -1, 1));
            robot.rightBack.setPower(Range.clip(rB, -1, 1));
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Touch", robot.blockToggle.isPressed());
            //telemetry.addData("X Pos: ", encoders.xDistance);
            //telemetry.addData("Y Pos: ", encoders.yDistance);
//          telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftFront.getPower(), );
            telemetry.update();

        }
    }
}
